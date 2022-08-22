#!/bin/bash
#
# Copyright (C) 2022 Patrice Brend'amour <patrice@brendamour.net>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365 -nodes -subj '/CN=jPasskittest'
openssl pkcs12 -export -in cert.pem -inkey key.pem -out jpasskittest.p12 -name "jpasskit"
rm key.pem cert.pem

