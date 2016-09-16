/*
 * Copyright (c) 2016-present Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://links.sonatype.com/products/clm/attributions.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package com.sonatype.nexus.ci.nxrm

class NexusPublisherWorkflowStepTest
    extends NexusPublisherDescriptorTest
{
  @Override
  NexusPublisherDescriptor getDescriptor() {
    return jenkins.getInstance().getDescriptorByType(NexusPublisherWorkflowStep.DescriptorImpl.class)
  }
}