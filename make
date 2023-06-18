#!/usr/bin/env bash

dests=("/Users/bobby/Library/Application Support/PrismLauncher/instances/personal 1.20/.minecraft/saves/test/scripts/" \
       "node1:~/scripts/")

LAST_RUN=$(<.make)

scripts=()
for file in ./*; do
    extension="${file##*.}"
    if [[ ${extension} == 'sc' && ${LAST_RUN} < $(date -r "${file}" +"%s") ]]; then
        scripts+=("${file}")
    fi
done

if [[ ${#scripts[@]} > 0 ]]; then
    for dest in "${dests[@]}"; do
        echo "copying:" "${scripts[@]}" "to" "${dest}"
        rsync -azvhP  "${scripts[@]}" "${dest}"
    done
    echo "your mom"
fi

date +"%s" > .make