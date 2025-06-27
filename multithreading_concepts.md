```markdown
# Operating System and Process

- OS provides virtual memory space on a process basis, each process having at least one thread.  
  - Threads share the process's virtual memory space.  
- OS controls access permissions per process, typically for file access.  
- JVM runs as a user-mode application (a process).

---

# Thread Basics

- Platform threads represent the execution unit running on CPU cores, each with its own independent flow.  
- A typical computer runs thousands of threads simultaneously.  
- The OS schedules CPU time for threads based on priority.  
- Context switching between threads incurs overhead.

---

# Why Multithreading?

### Fast single core vs slower multiple cores

- CPUs perform extremely fast computations while peripheral devices operate much slower.  
- CPU time can be wasted waiting on I/O operations.  
- Instead of increasing clock speed, CPUs have evolved by increasing the number of cores.  
- Multitasking maximizes CPU utilization by overlapping I/O and computation.

---

# Multithreading vs Multiprocessing

### Single Process + Multithreading

- All threads share the same virtual memory within the process.  
- Inter-thread communication (IPC) is unnecessary.  
- Threads share the process’s access permissions.  
- An error in one thread can terminate the entire process.

### Multiprocessing + Single Threading

- Each process has its own virtual memory and access permissions.  
- Inter-process communication requires IPC techniques.  
- Errors are confined within individual processes.

---

# Thread Attributes

- ID and Name  
- Priority: High, Normal, Low  
- Thread Grouping  

---

# Thread Priority

- Higher priority threads receive more CPU time.  
- Java priorities:  
  - `Thread.MAX_PRIORITY`  
  - `Thread.NORM_PRIORITY`  
  - `Thread.MIN_PRIORITY`  
- JVM controls actual scheduling policies.

---

# Thread States and Life Cycle

- `NEW`: Thread object created but not started.  
- `RUNNABLE`: Ready or running, waiting for CPU time.  
- `BLOCKED`: Waiting to acquire a lock.  
- `WAITING`: Waiting indefinitely (`wait()`, `join()`).  
- `TIMED_WAITING`: Waiting with timeout (`sleep()`, timed `wait()`).  
- `TERMINATED`: Execution finished.

Typical life cycle:

1. NEW → 2. RUNNABLE (via `start()`) → (BLOCKED / WAITING / TIMED_WAITING) → 3. TERMINATED

---

# File Processing and UI Separation

- In CLI environments, user input is an event.  
- Event-loop continuously handles these events.  
- File I/O and network operations are slow and unpredictable.  
- Separating slow I/O into separate threads keeps event-loop responsive.

---

# Daemon Threads

- Set via `Thread.setDaemon(true)` before `start()`.  
- Daemon threads terminate automatically when all user threads finish.  

---

