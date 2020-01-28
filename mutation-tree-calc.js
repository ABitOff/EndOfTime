const recipe = require('./src/resources/data/recipes/mutation_accelerator_tree.json');
const finalTree = {};
const groups = {};

const mutationTree = recipe.mutation_tree;
for (const node of mutationTree) {
    const specimen = node.specimen;
    if (!node.groups) continue;
    for (const group of node.groups) {
        let groupName, entryWeight;
        if (typeof group === 'string') {
            groupName = group;
            entryWeight = 1;
        } else {
            groupName = group.group;
            entryWeight = group.weight === undefined ? 1 : group.weight;
        }
        if (!groups[groupName]) groups[groupName] = [];
        groups[groupName].push([specimen, entryWeight]);
    }
}

for (const node of mutationTree) {
    const results = [];
    const specimen = node.specimen;
    for (const result of node.results) {
        let group, item, weight, mergeType;
        if (typeof result === 'string') {
            item = result;
            weight = 1;
            mergeType = 'last';
        } else {
            if (result.group ^ result.item) {
                console.log(result.group);
                console.log(result.item);
                throw new Error('Must not have group and item defined in a result!');
            }
            group = result.group;
            item = result.item;
            weight = result.weight === undefined ? 1 : result.weight;
            mergeType = result.merge === undefined ? 'last' : result.merge;
        }
        console.log(`${specimen}.${group ? group : item} = ${mergeType}(${weight}, other)`);
        results.push({
            group,
            item,
            weight,
            merge: mergeType
        });
    }

    const unique = {};
    let sum = 0;
    for (const r of results) {
        let group = r.group ? groups[r.group] : [
            [r.item, 1]
        ];
        for (const p of group) {
            let actualWeight = p[1] * r.weight;
            let actualValue;
            let previous = 0;
            if (unique[p[0]] === undefined) {
                actualValue = unique[p[0]] = actualWeight;
            } else {
                previous = unique[p[0]];
                if (r.merge.toLowerCase() === 'max') {
                    actualValue = Math.max(actualWeight, previous);
                } else if (r.merge.toLowerCase() === 'min') {
                    actualValue = Math.min(actualWeight, previous);
                } else if (r.merge.toLowerCase() === 'first') {
                    actualValue = previous;
                } else {
                    actualValue = actualWeight;
                }
                unique[p[0]] = actualValue;
                console.log(`for ${p[1]}, merging ${previous} and ${actualWeight}. old = ${previous}. new = ${actualValue}`);
            }
            sum += actualValue - previous;
        }

        finalTree[specimen] = Object.entries(unique).map(e => [e[0], e[1] / sum]);
    }
}

for (const e of Object.entries(finalTree)) {
    if (e[1].length === 0) continue;
    console.log(`${e[0]}:`);
    e[1] = e[1].sort((a, b) => {
        let cmp = Math.sign(b[1] - a[1]);
        if (cmp !== 0) return cmp;
        return a[0].localeCompare(b[0]);
    });
    for (const p of e[1]) {
        console.log(`\t${p[0]}: ${Math.round(p[1] * 100000) / 1000}%`);
    }
}
