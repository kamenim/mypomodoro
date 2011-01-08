#!/bin/bash
# vim: set sw=4 sts=4 et foldmethod=indent :
#You will need python to run this
LABELS="Feature"
SUMMARY="myPomodoro Executable Jar Zip"
PROJECT_NAME="mypomodoro"
python googlecode_upload.py -s "${SUMMARY}" -p "${PROJECT_NAME}" -l "${LABELS}" $@
