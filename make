#!/usr/bin/env bash

dests=("/Users/bobby/Library/Application Support/PrismLauncher/instances/personal 1.20/.minecraft/saves/test/scripts/" \
       "node1:~/scripts/")

scripts=()
for file in ./*; do
    extension="${file##*.}"
    if [[ ${extension} == 'sc' ]]; then
        scripts+=("${file}")
    fi
done

for dest in "${dests[@]}"; do
    echo "copying:" "${scripts[@]}" "to" "${dest}"
    rsync -azvhP  "${scripts[@]}" "${dest}"
done