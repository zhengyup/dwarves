package com.huskie.dwarves.survey.entity

import com.huskie.dwarves.organization.entity.Organization
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "survey")
class Survey (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "organization_id", nullable = false)
        val organization: Organization,

        @Column(nullable = false)
        val name: String,

        @Column
        val description : String? = null,

        @Column(name= "created at", nullable = false)
        val createdAt : LocalDateTime = LocalDateTime.now()
)