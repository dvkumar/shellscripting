#!/bin/bash
#Check if the script is executed by super user
#Script Executor User ID

#User Name
UID=$(id -u)

#Check for ROOT user
UID_ROOT='0'
if [[ "${UID}" -ne "${UID_ROOT}" ]]
then 
	echo "${U_NAME} is not a root user, Only ROOT user can execute this script"
        exit 1
fi

#Input argument check
if [[ "${#}" -lt 1 ]]
then
	echo "User Account setup failed for invalid # of arguments, supplied ${#}"
	echo "Usage: [User-NAME][User-Full-Name]....."
        exit 1  
fi

#create new user account
USER_NAME=${1}
shift
COMMENT="${@}"

useradd -c "{COMMENT}" -m ${USER_NAME}

if [[ "${?}" -ne 0 ]]
then
        echo "${USER_NAME} account setup failed!"
	exit 1
fi

#setup password for new user
PASSWORD="${RANDOM}${RANDOM}"
#SPECIAL_CHARACTER=$(echo '!@#$%^&*()_-+=' | fold -w1 | shuf | head -c1)

echo ${PASSWORD} | passwd  --stdin ${USER_NAME}

if [[ "${?}" -ne 0 ]]
then
	echo"Password setup for ${USER_NAME} failed"
	exit 1
fi

passwd -e ${USER_NAME}

printf "User successfully added on host:  $(hostname)\nUser Name: ${USER_NAME}\nPassword: ${PASSWORD}\n"
