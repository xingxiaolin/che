#
# Copyright (c) 2012-2018 Red Hat, Inc.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Red Hat, Inc. - initial API and implementation
#

unset PACKAGES
unset SUDO
command -v tar >/dev/null 2>&1 || { PACKAGES=${PACKAGES}" tar"; }
command -v curl >/dev/null 2>&1 || { PACKAGES=${PACKAGES}" curl"; }
test "$(id -u)" = 0 || SUDO="sudo -E"

AGENT_BINARIES_URI=https://codenvy.com/update/repository/public/download/org.eclipse.che.ls.test.binaries
CHE_DIR=$HOME/che
LS_DIR=${CHE_DIR}/test-ls
LS_LAUNCHER=${LS_DIR}/launch.sh

if [ -f /etc/centos-release ]; then
    FILE="/etc/centos-release"
    LINUX_TYPE=$(cat $FILE | awk '{print $1}')
 elif [ -f /etc/redhat-release ]; then
    FILE="/etc/redhat-release"
    LINUX_TYPE=$(cat $FILE | cut -c 1-8)
 else
    FILE="/etc/os-release"
    LINUX_TYPE=$(cat $FILE | grep ^ID= | tr '[:upper:]' '[:lower:]')
    LINUX_VERSION=$(cat $FILE | grep ^VERSION_ID=)
fi

MACHINE_TYPE=$(uname -m)

mkdir -p ${CHE_DIR}
mkdir -p ${LS_DIR}

########################
### Install packages ###
########################

# Red Hat Enterprise Linux 7
############################
if echo ${LINUX_TYPE} | grep -qi "rhel"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} yum install ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        curl --silent --location https://rpm.nodesource.com/setup_6.x | ${SUDO} bash -;
        ${SUDO} yum -y install nodejs;
    }

# Red Hat Enterprise Linux 6
############################
elif echo ${LINUX_TYPE} | grep -qi "Red Hat"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} yum install ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        curl --silent --location https://rpm.nodesource.com/setup_6.x | ${SUDO} bash -;
        ${SUDO} yum -y install nodejs;
    }


# Ubuntu 14.04 16.04 / Linux Mint 17
####################################
elif echo ${LINUX_TYPE} | grep -qi "ubuntu"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} apt-get update;
        ${SUDO} apt-get -y install ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        {
            curl -sL https://deb.nodesource.com/setup_6.x | ${SUDO} bash -;
        };

        ${SUDO} apt-get update;
        ${SUDO} apt-get install -y nodejs;
    }


# Debian 8
##########
elif echo ${LINUX_TYPE} | grep -qi "debian"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} apt-get update;
        ${SUDO} apt-get -y install ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        {
            curl -sL https://deb.nodesource.com/setup_6.x | ${SUDO} bash -;
        };

        ${SUDO} apt-get update;
        ${SUDO} apt-get install -y nodejs;
    }

# Fedora 23
###########
elif echo ${LINUX_TYPE} | grep -qi "fedora"; then
    command -v ps >/dev/null 2>&1 || { PACKAGES=${PACKAGES}" procps-ng"; }
    test "${PACKAGES}" = "" || {
        ${SUDO} dnf -y install ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        curl --silent --location https://rpm.nodesource.com/setup_6.x | ${SUDO} bash -;
        ${SUDO} dnf -y install nodejs;
    }


# CentOS 7.1 & Oracle Linux 7.1
###############################
elif echo ${LINUX_TYPE} | grep -qi "centos"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} yum -y install ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        curl --silent --location https://rpm.nodesource.com/setup_6.x | ${SUDO} bash -;
        ${SUDO} yum -y install nodejs;
    }

# openSUSE 13.2
###############
elif echo ${LINUX_TYPE} | grep -qi "opensuse"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} zypper install -y ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        ${SUDO} zypper ar http://download.opensuse.org/repositories/devel:/languages:/nodejs/openSUSE_13.1/ Node.js
        ${SUDO} zypper in nodejs
    }

# Alpine 3.3
############
elif echo ${LINUX_TYPE} | grep -qi "alpine"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} apk update
        ${SUDO} apk add ${PACKAGES};
    }

    command -v nodejs >/dev/null 2>&1 || {
        ${SUDO} apk update
        ${SUDO} apk add nodejs;
    }

else
    >&2 echo "Unrecognized Linux Type"
    >&2 cat $FILE
    exit 1
fi


#######################
### Install Test LS ###
#######################

curl -s ${AGENT_BINARIES_URI} | tar xzf - -C ${LS_DIR}

touch ${LS_LAUNCHER}
chmod +x ${LS_LAUNCHER}
echo "${LS_DIR}/test-ls/server.sh" > ${LS_LAUNCHER}
