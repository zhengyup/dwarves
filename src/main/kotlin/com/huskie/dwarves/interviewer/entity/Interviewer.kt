package com.huskie.dwarves.interviewer.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "interviewer")
data class Interviewer(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(name = "name", nullable = false)
        val name: String,

        @Column(name = "email")
        val email: String? = null,

        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now()
)