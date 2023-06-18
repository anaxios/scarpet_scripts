#!/usr/bin/env bash


CONF_FOLDER=".make"

if [[ ! -d ${CONF_FOLDER}  ]]; then
    mkdir "${CONF_FOLDER}"
fi

if [[ ! -f ${CONF_FOLDER}/destinations  ]]; then
    touch "${CONF_FOLDER}/destinations"
fi

if [[ ! -f ${CONF_FOLDER}/lastrun  ]]; then
    touch "${CONF_FOLDER}/lastrun"
fi

while IFS= read -r path; do
    dests+=("${path[@]}")
done < "${CONF_FOLDER}/destinations"

LAST_RUN=$(<./"${CONF_FOLDER}"/lastrun)
scripts=()

for file in ./*; do
    extension="${file##*.}"
    if [[ ${extension} == 'sc' && ${LAST_RUN} < $(date -r "${file}" +"%s") ]]; then
        scripts+=("${file}")
    fi
done

if [[ ${#scripts[@]} > 0 && ${#dests[@]} > 0 ]]; then
    for dest in "${dests[@]}"; do
        echo "copying:" "${scripts[@]}" "to" "${dest}"
        rsync -azvhP  "${scripts[@]}" "${dest}/"
    done
fi

date +"%s" > ./"${CONF_FOLDER}/lastrun"