const recipe = require('./src/resources/data/recipes/mutation_accelerator_tree.json');
const finalTree = {};
const groups = {};

const mutationTree = recipe.mutation_tree.sort((a, b) => a.specimen.localeCompare(b.specimen));
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

const groupInv = {};
for (const group of Object.entries(groups)) {
    for (const pair of group[1]) {
        if (groupInv[pair[0]]) groupInv[pair[0]].push(group[0]);
        else groupInv[pair[0]] = [group[0]];
    }
}
for (const item of Object.entries(groupInv)) {
    console.log(`"${item[0]}": ["${item[1].join('", "')}"],`);
}
console.log();

for (const node of mutationTree) {
    const results = [];
    const specimen = node.specimen;
    let logged = false;
    for (const result of node.results) {
        let group, item, weight, mergeType;
        if (typeof result === 'string') {
            item = result;
            weight = 1;
            mergeType = 'new';
        } else {
            if (!(result.group !== undefined ^ result.item !== undefined)) {
                console.log(result.group);
                console.log(result.item);
                throw new Error('Must not have group and item defined in a result!');
            }
            group = result.group;
            item = result.item;
            weight = result.weight === undefined ? 1 : result.weight;
            mergeType = result.merge === undefined ? 'new' : result.merge;
        }
        console.log(`${specimen}.${group ? group : item} = ${mergeType}(${weight})`);
        logged = true;
        results.push({
            group,
            item,
            weight,
            merge: mergeType
        });
    }

    const unique = {};
    let sum = 0;
    let merges = [];
    const mergesStrLengths = [];
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
                } else if (r.merge.toLowerCase() === 'old') {
                    actualValue = previous;
                } else {
                    actualValue = actualWeight;
                }
                unique[p[0]] = actualValue;
                const merge = [specimen, p[0], String(previous), String(actualWeight), r.merge, String(actualValue)];
                merges.push(merge);
                mergesStrLengths.push(merge.map(s => s.length));
            }
            sum += actualValue - previous;
        }
    }

    const maxMergesStrLengths = mergesStrLengths.reduce((a, b) => [
        Math.max(a[0], b[0]), Math.max(a[1], b[1]), Math.max(a[2], b[2]),
        Math.max(a[3], b[3]), Math.max(a[4], b[4]), Math.max(a[5], b[5]),
    ], [0, 0, 0, 0, 0, 0]);
    merges = merges.map(m => [
        pad(m[0], maxMergesStrLengths[0]), pad(m[1], maxMergesStrLengths[1]), pad(m[2], maxMergesStrLengths[2]),
        pad(m[3], maxMergesStrLengths[3]), pad(m[4], maxMergesStrLengths[4]), pad(m[5], maxMergesStrLengths[5]),
    ])
    for (const merge of merges) {
        console.log(`for ${merge[0]}'s ${merge[1]} result, merging ${merge[2]} and ${merge[3]} using ${merge[4]}. old = ${merge[2]} new = ${merge[5]}`);
        logged = true;
    }
    if (logged) console.log(); // \n

    finalTree[specimen] = Object.entries(unique).map(e => [e[0], e[1] / sum]);
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

function pad(s, n) {
    while (s.length < n) {
        s += ' ';
    }
    return s;
}
