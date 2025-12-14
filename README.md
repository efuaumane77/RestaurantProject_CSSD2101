# RestaurantProject_CSSD2101
This is the final project for the java course 2101 CSSD. Its a restaurant system that applies OOP principles and the Design Patterns created by the GangofFour (GoF).

 * Restaurant Management System
 * ---------------------------------------------------------------------------------
 * Demonstrates an auditable, layered architecture for restaurant operations including
 * menu management, order processing, reservations, staff roles, and inventory tracking
 * with comprehensive business rule enforcement and compliance logging.
 *
 * Layers:
 *   • Domain model (MenuItem hierarchy + Order + Reservation + Staff roles + Inventory)
 *   • Repositories (MenuRepository, OrderRepository, ReservationRepository, etc.)
 *   • Application services (MenuService, OrderService, ReservationService, InventoryService)
 *   • Secure tamper-evident audit ledger (hash-chained entries)
 *   • Test harness (main)
 */
