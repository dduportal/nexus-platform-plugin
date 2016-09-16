/*
 * Copyright (c) 2016-present Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://links.sonatype.com/products/clm/attributions.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package com.sonatype.nexus.ci.config

import com.sonatype.nexus.api.exception.RepositoryManagerException
import com.sonatype.nexus.ci.util.FormUtil
import com.sonatype.nexus.ci.util.RepositoryManagerClientUtil

import hudson.Extension
import hudson.util.FormValidation
import hudson.util.FormValidation.Kind
import hudson.util.ListBoxModel
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

class Nxrm2Configuration
    extends NxrmConfiguration
{
  @DataBoundConstructor
  Nxrm2Configuration(final String id, final String internalId, final String displayName, final String serverUrl,
                     final String credentialsId)
  {
    this.id = id
    this.internalId = internalId
    this.displayName = displayName
    this.serverUrl = serverUrl
    this.credentialsId = credentialsId
  }

  @Extension
  public static class DescriptorImpl
      extends NxrmConfiguration.NxrmDescriptor
  {
    public DescriptorImpl() {
      super(Nxrm2Configuration.class)
    }

    @Override
    public String getDisplayName() {
      return 'Nexus Repository Manager 2.x Server'
    }

    @SuppressWarnings('unused')
    public FormValidation doCheckDisplayName(@QueryParameter String value, @QueryParameter String internalId) {
      def globalConfigurations = GlobalNexusConfiguration.all().get(GlobalNexusConfiguration.class)
      for (NxrmConfiguration config : globalConfigurations.nxrmConfigs) {
        if (!config.internalId.equals(internalId) && config.displayName.equals(value)) {
          return FormValidation.error('Display Name must be unique')
        }
      }
      return FormUtil.validateNotEmpty(value, 'Display Name is required')
    }

    @SuppressWarnings('unused')
    public FormValidation doCheckId(@QueryParameter String value, @QueryParameter String internalId) {
      def globalConfigurations = GlobalNexusConfiguration.all().get(GlobalNexusConfiguration.class)
      for (NxrmConfiguration config : globalConfigurations.nxrmConfigs) {
        if (!config.internalId.equals(internalId) && config.id.equals(value)) {
          return FormValidation.error('Server ID must be unique')
        }
      }
      def validation = FormUtil.validateNoWhitespace(value, 'Server ID must not contain whitespace')
      if (validation.kind == Kind.OK) {
        validation = FormUtil.validateNotEmpty(value, 'Server ID is required')
      }
      return validation
    }

    @SuppressWarnings('unused')
    public FormValidation doCheckServerUrl(@QueryParameter String value) {
      def validation = FormUtil.validateUrl(value)
      if (validation.kind == Kind.OK) {
        validation = FormUtil.validateNotEmpty(value, 'Server Url is required')
      }
      return validation
    }

    @SuppressWarnings('unused')
    public ListBoxModel doFillCredentialsIdItems(@QueryParameter String serverUrl) {
      return FormUtil.buildCredentialsItems(serverUrl)
    }

    @SuppressWarnings('unused')
    public FormValidation doVerifyCredentials(
        @QueryParameter String serverUrl,
        @QueryParameter String credentialsId) throws IOException
    {
      try {
        // TODO: Validate NXRM credentials
        def client = RepositoryManagerClientUtil.buildRmClient(serverUrl, credentialsId)
        def repositories = client.getRepositoryList()

        return FormValidation.
            ok("Nexus Repository Manager 2.x connection succeeded (${repositories.size()} Maven repositories)")
      }
      catch (RepositoryManagerException e) {
        return FormValidation.error(e, 'Nexus Repository Manager 2.x connection failed');
      }
    }
  }
}