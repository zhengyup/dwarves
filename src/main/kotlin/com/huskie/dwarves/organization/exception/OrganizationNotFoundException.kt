package com.huskie.dwarves.organization.exception

class OrganizationNotFoundException(id: Long) :
        RuntimeException("Organization with id $id not found")