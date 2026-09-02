# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

**Primary:** Students and teachers at a single university who need to share and access course materials (lecture slides, notes, assignments, reference files).

**Secondary:** Platform administrators (likely faculty or IT staff) who onboard users and moderate content.

## Product Purpose

Replace scattered file sharing (WeChat groups, email attachments, USB drives) with a single, organized repository of course materials that students and teachers can publish, browse, and discover — while ensuring shared resources are reviewed for quality before reaching the broader audience.

## Positioning

A centralized, vetted resource library for a university community, combining the convenience of self-service publishing with admin quality control — so students find what they need in one place and trust what they find.

## Operating Context

- Used primarily during semesters alongside coursework — students searching for specific course materials, teachers distributing resources.
- Desktop-first web app (not a mobile app). Likely accessed from campus labs, library, or personal laptops.
- Admins periodically batch-import new users from Excel (e.g., at semester start).
- Resources are files (PDF, PPT, DOC, images) tied to a course name.

## Capabilities and Constraints

**Confirmed:**
- User registration (student/teacher role) and login
- Role-based routing: students/teachers access dashboard + resources; admins additionally access user management
- Resource CRUD: publish (with file upload), edit, delete
- Resource lifecycle: pending → approved / rejected (admin review)
- Resource preview (PDF/images inline; PPT/Word noted as future)
- Favorite/bookmark resources
- Admin user management: list, edit, delete, reset password
- Batch user import from Excel (.xlsx)
- Profile editing and password change
- JWT-based auth, token stored in localStorage

**Undecided:**
- Whether guest browsing (mentioned in the Home page) is intended to work pre-authentication
- Whether the platform tracks downloads or usage analytics
- Whether courses are free-form text or sourced from a controlled list

## Brand Commitments

- Name: **青知共享平台** (Qingzhi Sharing Platform / "Youth Knowledge")
- Tagline: **开放共享 · 知识同行** (Open Sharing · Knowledge Together)
- No existing logo, color system, or institutional brand constraints — free rein on visual identity.

## Evidence on Hand

- Full Vue 3 frontend implementation in this repository with views for Home, Login, Register, Dashboard, Resources, and User management.
- No backend or API documentation available; interface contracts are inferred from Axios calls and TypeScript types in `src/api/` and `src/types/api.ts`.

## Product Principles

1. **Trust through review.** Every published resource passes admin moderation before it reaches the general listing — quality over quantity.
2. **Role‑appropriate surfaces.** Students and teachers see what they need (resources); admins see what they need (users + moderation). No cognitive clutter from irrelevant tools.
3. **Low‑friction publishing.** Uploading a resource should take seconds — title, file, course. Metadata is minimal; the review gate handles quality.
4. **One institution, one source of truth.** The platform serves a single university community, so enrollment data can be batch-imported and user identities are real names tied to student/faculty IDs.

## Accessibility & Inclusion

No product-specific accessibility requirements were established.
