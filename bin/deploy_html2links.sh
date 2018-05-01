#!/bin/bash

cd html2links

~/google-cloud-sdk/bin/gcloud beta functions deploy html2links --trigger-http

~/google-cloud-sdk/bin/gcloud compute project-info describe --project html2links

~/google-cloud-sdk/bin/gcloud config set project

~/google-cloud-sdk/bin/gcloud config list

~/google-cloud-sdk/bin/gcloud projects list

cd ..

