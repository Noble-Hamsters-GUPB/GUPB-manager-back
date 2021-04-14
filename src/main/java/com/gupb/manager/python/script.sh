#!/usr/bin/env bash
#
# This file runs gupb module. call params:
# * path_to_gupb_parent (absolute)
# * name_of_gupb_dir (relative)
#

runner_dir=$1
runner_dir=${runner_dir%\"}
runner_dir=${runner_dir#\"}
gupb_dir_name=$2
gupb_dir_path=$runner_dir/$gupb_dir_name
venv_name=GUPB-venv
setup_logs_dir=$runner_dir/setup_logs_dir
current_time_stamp=$(date +"%d_%m_%y")__$(date +"%H_%M_%S")
setup_logs_file=$setup_logs_dir/$(echo "$current_time_stamp")_setup_log.txt

if [ ! -d "$setup_logs_dir" ]; then
    mkdir $setup_logs_dir
fi

cd "$runner_dir" || exit
cd "$gupb_dir_name" || exit

python3 -m pip install virtualenv > "$setup_logs_file"

python3 -m venv $venv_name >> "$setup_logs_file" || exit

source $venv_name/bin/activate || exit

python3 -m pip install --upgrade pip >> "$setup_logs_file"
python3 -m pip install -r "$gupb_dir_path/requirements.txt" >> "$setup_logs_file"

# Execution
python3 -m pip install --upgrade pip >> "$setup_logs_file"

echo "Execution starting" >&2
python3 -m gupb

deactivate

echo "Done" >> "$setup_logs_file"
