package org.example.domain;
import java.awt.*;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

sealed interface StaffRole permits Manager, Waiter, Chef{
    String id();
    String name();
}

record Manager(String id, String name) implements StaffRole{
    boolean canApproveDiscounts(){ return true;}
    boolean canManageInventory(){return true;}
}

record Waiter(String id, String name) implements StaffRole{
    boolean canTakeOrders(){return true;}
}

record Chef(String id, String name) implements StaffRole{
    boolean canPrepareOrders(){return true;}
}


public class Staff {
}
