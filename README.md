# 🍽️ Restaurant Simulation Project

## 📌 Introduction

This project is a multi-threaded simulation of a restaurant where several entities — **chefs**, **waiters**, and **customers** — operate concurrently. The focus is on implementing **synchronization mechanisms** to manage access to shared resources like tables, the kitchen, and orders.

---

## 🧾 Project Description

In this simulation:

- Customers arrive and wait for available tables.
- Chefs prepare meals only when there's a pending order.
- Waiters serve prepared meals and clear tables once customers leave.

### Core Challenges

- 🪑 Ensure exclusive access to tables (one customer per table).
- 👨‍🍳 Chefs must not overproduce — meals should match orders.
- 🧑‍💼 Waiters should only serve meals that are ready.
- 🚶‍♂️ Customers can only leave after being served.

---

## 👥 Entities and Responsibilities

### 👤 Customers
- Arrive and wait if no tables are available.
- Place orders once seated.
- Wait for the meal, eat, and leave.

### 👨‍🍳 Chefs
- Pick up orders as they arrive.
- Prepare meals accordingly.
- Notify waiters when the food is ready.

### 🧑‍💼 Waiters
- Wait for notifications from chefs.
- Serve meals to corresponding customers.
- Clear tables when customers leave.

### 🔗 Shared Resources
- **Tables**: Limited and shared among customers.
- **Orders**: Customers place orders; chefs consume them.
- **Cooked Meals**: Prepared meals are queued for waiters.

---

## 🧵 Synchronization Mechanisms

This simulation uses **Java concurrency** tools and follows the **Producer-Consumer** paradigm with semaphores and queues:

- Tables: Controlled to avoid double-seating.
- Orders: Only consumed when present.
- Meals: Only served when prepared.

---

## 🛠️ Requirements

- **Language**: Java (with Java Concurrency APIs)
- **Design**: Based on UML diagram (Appendix I)
- **Mechanisms**: Semaphores, queues, synchronization
- **Simulation**: Random delays for realism (arrival, eating, prep)
- **Output**: Real-time log of all key events

---

## 📥 Input Format

Your program reads from a structured text file:

```txt
NC=2 NW=3 NT=4
Burger=00:8 Pizza=00:10 Pasta=00:10 Salad=00:5 Steak=00:8 Sushi=00:5 Tacos=00:5 Soup=00:10
CustomerID=1 ArrivalTime=08:00 Order=Burger
CustomerID=2 ArrivalTime=08:02 Order=Pizza

