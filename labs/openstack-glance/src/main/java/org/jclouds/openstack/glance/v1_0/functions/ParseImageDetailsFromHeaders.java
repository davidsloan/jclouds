/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.glance.v1_0.functions;

import static org.jclouds.openstack.glance.v1_0.options.ImageField.*;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.domain.Image.Status;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * This parses {@link ImageDetails} from HTTP headers.
 * 
 * @author Adrian Cole
 */
public class ParseImageDetailsFromHeaders implements Function<HttpResponse, ImageDetails> {
   private final DateService dateService;

   @Inject
   public ParseImageDetailsFromHeaders(DateService dateService) {
      this.dateService = dateService;
   }

   public ImageDetails apply(HttpResponse from) {
      ImageDetails.Builder<?> builder = ImageDetails.builder()
                .id(from.getFirstHeaderOrNull(ID.asHeader()))
                .name(from.getFirstHeaderOrNull(NAME.asHeader()))
                .checksum(Optional.fromNullable(from.getFirstHeaderOrNull(CHECKSUM.asHeader())))
                .minDisk(Long.parseLong(from.getFirstHeaderOrNull(MIN_DISK.asHeader())))
                .minRam(Long.parseLong(from.getFirstHeaderOrNull(MIN_RAM.asHeader())))
                .isPublic(Boolean.parseBoolean(from.getFirstHeaderOrNull(IS_PUBLIC.asHeader())))
                .createdAt(dateService.iso8601SecondsDateParse(from.getFirstHeaderOrNull(CREATED_AT.asHeader())))
                .updatedAt(dateService.iso8601SecondsDateParse(from.getFirstHeaderOrNull(UPDATED_AT.asHeader())))
                .owner(Optional.fromNullable(from.getFirstHeaderOrNull(OWNER.asHeader())))
                .location(Optional.fromNullable(from.getFirstHeaderOrNull(LOCATION.asHeader())))
                .status(Status.fromValue(from.getFirstHeaderOrNull(STATUS.asHeader())));

      String containerFormat = from.getFirstHeaderOrNull(CONTAINER_FORMAT.asHeader());
      String diskFormat = from.getFirstHeaderOrNull(DISK_FORMAT.asHeader());
      String deletedAt = from.getFirstHeaderOrNull(DELETED_AT.asHeader());
      String size = from.getFirstHeaderOrNull(SIZE.asHeader());

      if (containerFormat != null) builder.containerFormat(ContainerFormat.fromValue(containerFormat));
      if (diskFormat != null) builder.diskFormat(DiskFormat.fromValue(diskFormat));
      if (deletedAt != null) builder.deletedAt(dateService.iso8601SecondsDateParse(deletedAt));
      if (size != null) builder.size(Long.parseLong(size));

      return builder.build();
   }
}
