package com.huskie.dwarves.organization.entity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "organization")
class Organization (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(unique = true, nullable = false)
    val code: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)