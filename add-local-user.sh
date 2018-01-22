#!/bin/bash
#Check if the script is executed by super user
#Script Executor User ID


#User Name
UID=$(id -u)

#User real name
U_NAME=$(id -unr)
#echo "Your user name is ${U_NAME}"

#Check for ROOT user
UID_ROOT='0'
if [[ "${UID}" -ne "${UID_ROOT}" ]]
then 
	echo "${U_NAME} is not a root user, Only ROOT user can execute this script"
        exit 1
fi

#get the username
read -p 'Enter user name to create: ' USER_NAME

read -p 'Enter user full name: ' COMMENT

#create new user account
useradd -c "{COMMENT}" -m "${USER_NAME}"

if [[ "${?}" -ne 0 ]]
then
	echo "${USER_NAME} account creation failed!"
	exit 1
fi

#setup password for new user
read -p 'Enter password: ' PASSWORD

echo ${PASSWORD} | passwd  --stdin ${USER_NAME}

if [[ "${?}" -ne 0 ]]
then
	echo "${USER_NAME} password creation failed"
	rm -R -f /home/${USER_NAME}
	userdel ${USER_NAME}
	exit 1
fi

passwd -e ${USER_NAME}

printf "User successfully added on host:  $(hostname)\nUser Name: ${USER_NAME}\nPassword: ${PASSWORD}\n" 
