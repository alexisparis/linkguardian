#!/bin/bash

echo "#########################"
echo "http://linkguardian.io =>"
echo "-------------------------"
curl -I http://linkguardian.io
echo "-------------------------"
echo "should be 301 to http://www.linkguardian.io"

echo ""
echo "#########################"
echo "http://www.linkguardian.io =>"
echo "-------------------------"
curl -I http://www.linkguardian.io
echo "-------------------------"
echo "should be 301 to https://www.linkguardian.io"

echo ""
echo "#########################"
echo "https://www.linkguardian.io =>"
echo "-------------------------"
curl -I https://www.linkguardian.io
echo "-------------------------"
echo "should be 200 no redirect"

echo ""
echo "#########################"
echo "https://linkguardian.io =>"
echo "-------------------------"
curl -I https://linkguardian.io
echo "-------------------------"
echo "should be 200 no redirect"

echo ""
echo "FINISHED!!!"
