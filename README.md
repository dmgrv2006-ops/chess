# Chess Engine

A fully functional chess engine built in Java, developed as a personal project during my second semester of Computer Science. 

## Features

### Game
- Complete chess rule implementation including castling, en passant, and promotion
- All draw conditions: stalemate, 50-move rule, insufficient material, threefold repetition (via Zobrist hashing)

### AI
- **Minimax** with **alpha-beta pruning**
- **Move ordering** (MVV-LVA) for better pruning efficiency
- **Quiescence search** to avoid the horizon effect
- **PeSTO tapered evaluation** — piece-square tables blended between middlegame and endgame based on remaining material
- King position tracking for fast check detection

## Project Structure

- `Main.java` — game loop and user input
- `Game.java` — board representation, move generation, draw detection
- `Move.java` — move representation
- `MoveIterator.java` — iterator for legal moves
- `ChessAI.java` — minimax search and evaluation

## Algorithm Overview

### Search
The engine uses **minimax with alpha-beta pruning** — it searches all possible moves up to a fixed depth, assuming both sides play optimally. Alpha-beta pruning eliminates branches that cannot affect the final result, dramatically reducing the number of positions evaluated.

**Move ordering** ensures the best moves (captures ordered by MVV-LVA) are searched first, maximizing pruning efficiency.

**Quiescence search** extends the search at leaf nodes to resolve captures, preventing the engine from misjudging positions where a piece is about to be taken.

### Evaluation
The engine uses **PeSTO's evaluation function** — a tapered evaluation that blends middlegame and endgame piece-square tables based on the amount of material remaining. This gives the engine positional understanding beyond simple material counting.

### Draw Detection
Threefold repetition is detected using **Zobrist hashing** — each position is assigned a unique 64-bit hash computed incrementally on every move, allowing fast position comparison without scanning the board.
