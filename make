#!/usr/bin/env bash

dests=("/Users/bobby/Library/Application Support/PrismLauncher/instances/personal 1.20/.minecraft/saves/test/scripts/" \
       "node1:~/scripts/")

for dest in "${dests[@]}"; do
    for file in ./*; do
        extension="${file##*.}"
        if [[ ${extension} == 'sc' ]]; then
            echo "copying:" "${file}" "to" "${dest}"
            rsync -azvhP  "${file}" "${dest}"
        fi
    done
done