package com.huskie.dwarves.organization.exceptions

class OrganizationNotFoundException(id: Long) :
        RuntimeException("Organization with id $id not found")