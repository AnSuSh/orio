# Orio 🪙
### A Modern, Reactive Personal Finance Tracker

Orio is a high-performance Android expense manager built with **Jetpack Compose**. It focuses on a seamless user experience, utilizing a fully reactive data pipeline to help users track, filter, and analyze their spending habits in real-time.

---

## 🚀 Key Features

*   **Reactive Filtering & Search:** Real-time transaction filtering by Category, Note (Search), and Type (Income/Expense) using a debounced StateFlow pipeline.
*   **Dynamic Budgeting:** Interactive monthly budget tracking that updates instantly as transactions are added or modified.
*   **Professional Data Visualization:** Color-coded markers and category-specific iconography for instant scanability of financial records.
*   **Advanced Date Management:** Full support for historical logging with a Material 3 DatePicker integration.
*   **Fluid UX:** Optimized with **Sticky Headers** for date grouping, **Swipe-to-Dismiss** gestures, and smooth `animateItem()` spring physics for list reordering.
*   **Offline First:** Built on a robust Room Database architecture to ensure data is always accessible and private.

---

## 🛠 Tech Stack & Architecture

Orio is built using the latest industry standards to ensure scalability and maintainability:

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture principles.
*   **Concurrency:** Kotlin Coroutines & Flow (StateFlow, SharedFlow, Combine).
*   **Dependency Injection:** Hilt (Dagger)
*   **Local Storage:** Room Database
*   **Navigation:** Compose Navigation with Type-Safety.
*   **Lifecycle:** `collectAsStateWithLifecycle` for lifecycle-aware UI updates.

---

## 🏗 High-Level Architecture Overview

Orio follows **Unidirectional Data Flow (UDF)**. The UI reacts to state changes emitted by the ViewModel, which combines multiple data streams from the Repository layer.

### The Reactive Pipeline
One of the core highlights of this project is the **Filtered Transaction Pipeline**. Instead of manual list management, Orio uses the `combine` operator to merge the raw database flow with the user's filter state:

```kotlin
// Example of the reactive logic used in the project
val filteredTransactions = combine(
    repository.getAllTransactions(),
    filterState.debounce { if (it.searchQuery.isNotEmpty()) 300L else 0L }
) { transactions, filters ->
    transactions
        .filter { it.matches(filters) }
        .applySort(filters.sortBy)
}.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
```

---

## 🧠 Technical Highlights
1. Performance Optimization: Utilized `derivedStateOf` to prevent expensive re-grouping of lists during recompositions.

2. Custom Animations: Implemented custom Spring stiffness and damping ratios in `LazyColumn` for a "premium" feel during filtering and sorting.

3. Modern Concurrency: Heavy lifting (sorting/filtering) is offloaded to `Dispatchers.Default` using `withContext` to ensure the Main thread remains jank-free.

4. UX Polish: Integrated `LocalFocusManager` and `LocalHapticFeedback` to provide physical and visual confirmation on user actions.

---

## 📲 Installation
1. Clone the repository:

```Bash
git clone [https://github.com/yourusername/orio-finance.git](https://github.com/yourusername/orio-finance.git)
```
2. Open the project in Android Studio Hedgehog or newer.

3. Sync Gradle and run the app on an emulator or physical device (API 24+).

---

## 🤝 Contact
[Shrinivasa Manjithaya]

[LinkedIn Profile](https://www.linkedin.com/in/shrinivasa-manjithaya-6855b6157)

[Email Address](amanjithayas@gmail.com)

Developed with ❤️ using Jetpack Compose.
