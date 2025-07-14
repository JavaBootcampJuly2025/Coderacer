# CodeRacer

A “TypeRacer” for code - practice writing code as fast and as accurately as possible.

---

## Table of Contents

1. [Features](#features)  
2. [Tech Stack](#tech-stack)  
3. [Architecture Overview](#architecture-overview)  
4. [Data Models](#data-models)  
5. [API Endpoints](#api-endpoints)  

---

## Features

- Practice typing code snippets, track speed & accuracy
- Get post-game analysis and other individually tailored metrics for self assessment
- Difficulty tiers: *easy*, *medium*, *hard*  
- Per-programming-language and per-tag level choice  
- Compete with other users on the leaderboards
- Multiplayer (?)

---

## Tech Stack

- **Backend**: _Spring Boot_  
- **Frontend**: _React_  
- **Database**: _PostgreSQL_  
- **Auth**: _JWT_  
- **Hosting/CI**: _GitHub Actions, Microsoft Azure_

---

## Architecture Overview

[ Frontend ] ⇄ [ REST / WebSocket API ] ⇄ [ Backend Services ] ⇄ [ Database ]


- **Frontend**  
  - Profile dashboard
  - Leaderboards
  - Code typing game

- **Backend**  
  - Controllers
  - Services
  - Repositories

- **Database**  
  - Tables for accounts, levels, level sessions; views for leaderboard entries and account metrics  

---

## Data Models