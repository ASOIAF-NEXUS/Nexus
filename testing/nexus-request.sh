#!/bin/bash

NEXUS_URL="http://localhost:8080/api/v1"

SCRIPT_NAME=`basename $0`
SCRIPT_DIR=`dirname $0`

if [ -z "${1}" ]; then

	echo -e "\n\nUsage: ${SCRIPT_NAME} <api_endpoint> [post_body_filename]\n\n"
	echo -e "GET example: ${SCRIPT_NAME} /listbuilder/units\n"
	echo -e "POST example: ${SCRIPT_NAME} /validation/validate req/post-validate.json\n"

	exit 1
fi

CURL_METHOD="GET"
CURL_POST_ARGS=""

if [ -n "${2}" ]; then
	CURL_METHOD="POST"
	CURL_POST_ARGS="-d @${2}"
fi

if [ ! -f token.txt ]; then

curl -X POST "${NEXUS_URL}/users/signup" \
     -H "Content-Type: application/json" \
     -d @${SCRIPT_DIR}/req/post-signup.json

curl -X POST "${NEXUS_URL}/users/login" \
     -H "Content-Type: application/json" \
     -d @${SCRIPT_DIR}/req/post-login.json > token.txt

fi

JWT=`[ -f token.txt ] && cat token.txt || echo -n ''`

curl -v -X ${CURL_METHOD} "${NEXUS_URL}/${1}" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $JWT" \
     ${CURL_POST_ARGS}


