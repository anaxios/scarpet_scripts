#!/usr/bin/env bash

### Destinations file is a return separated file and must end with a blank line for all lines to be read

CONF_FOLDER=".make"
DESTS=()
SCRIPTS=()

setup_dotfolder() {
    if [[ ! -d ${CONF_FOLDER}  ]]; then
        mkdir "${CONF_FOLDER}"
    fi

    if [[ ! -f ${CONF_FOLDER}/destinations  ]]; then
        touch "${CONF_FOLDER}/destinations"
    fi

    if [[ ! -f ${CONF_FOLDER}/lastrun  ]]; then
        touch "${CONF_FOLDER}/lastrun"
    fi
}

read_destination_file() {
    while IFS= read -r path; do
        DESTS+=("${path[@]}")
    done < "${CONF_FOLDER}/destinations"
}

get_modified_scripts() {
    LAST_RUN=$(<./"${CONF_FOLDER}"/lastrun)
    for file in ./*; do
        extension="${file##*.}"
        if [[ ${extension} == 'sc' && ${LAST_RUN} < $(date -r "${file}" +"%s") ]]; then
            SCRIPTS+=("${file}")
        fi
    done
    date +"%s" > ./"${CONF_FOLDER}/lastrun"
}

send_scripts_to_destinations() {
    if [[ ${#SCRIPTS[@]} > 0 && ${#DESTS[@]} > 0 ]]; then
        for dest in "${DESTS[@]}"; do
            echo "copying:" "${SCRIPTS[@]}" "to" "${dest}"
            rsync -azvhP  "${SCRIPTS[@]}" "${dest}/"
        done
    fi
}

setup_dotfolder
read_destination_file
get_modified_scripts
send_scripts_to_destinations