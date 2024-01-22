#
# Copyright © 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM eclipse-temurin:17-jdk-alpine
RUN addgroup -S spring \
	&& adduser -S spring -G spring
USER spring:spring
VOLUME /demo/data
WORKDIR /demo
ENV spring_profiles_active=docker
ARG JAR_FILE
ADD ${JAR_FILE} /demo/auth-server.jar
EXPOSE 9000
ENTRYPOINT ["java","-Dloader.path=file:/demo/data/lib","-Djava.security.egd=file:/dev/./urandom", "-jar","/demo/auth-server.jar"]
