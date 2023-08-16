
import java.text.*;
import java.util.*;

class Marriage {
    Date marriageDate;
    Date divorceDate;

    public Marriage(Date marriageDate, Date divorceDate) {
        this.marriageDate = marriageDate;
        this.divorceDate = divorceDate;
    }
}

class Person {
    String name;
    String relation;
    Date dateOfBirth;
    Date dateOfDeath;
    ArrayList<Person> children;

    public Person(String name, Date dateOfBirth, Date dateOfDeath, String relation) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.relation = relation;
        this.children = new ArrayList<>();
    }
}

class Deceased extends Person {
    String gender;
    String placeOfBirth;
    List<Marriage> marriages = new ArrayList<>();
    ArrayList<Person> familyMembers = new ArrayList<>();

    public Deceased(String name, Date dateOfBirth, Date dateOfDeath, String gender, String placeOfBirth, String relation) {
        super(name, dateOfBirth, dateOfDeath, relation);
        this.gender = gender;
        this.placeOfBirth = placeOfBirth;
    }

    public void addMarriage(Marriage marriage) {
        marriages.add(marriage);
    }

    public void addFamilyMember(Person person) {
        familyMembers.add(person);
    }

    public void displayDetails() {
        System.out.println("\nDeceased: " + this.name);
        System.out.println("Date of Birth: " + this.dateOfBirth.toString());
        System.out.println("Date of Death: " + this.dateOfDeath.toString());
        System.out.println("Gender: " + this.gender);
        System.out.println("Place of Birth: " + this.placeOfBirth);

        System.out.println("\nMarriage and Divorce History:");
        if (this.marriages.isEmpty()) {
            System.out.println("The deceased was not married.");
        } else {
            for (int i = 0; i < this.marriages.size(); i++) {
                Marriage marriage = this.marriages.get(i);
                System.out.println("Marriage " + (i + 1) + ":");
                System.out.println("  Marriage Date: " + marriage.marriageDate);
                if (marriage.divorceDate != null) {
                    System.out.println("  Divorce Date: " + marriage.divorceDate);
                } else {
                    System.out.println("  Divorce Date: Not divorced");
                }
            }
        }
    }

    public void displayFamilyDetails() {
        System.out.println("Family Members:");
        for (Person member : this.familyMembers) {
            System.out.println("Name of " + member.relation + ": " + member.name);
            System.out.println("Dob of " + member.relation + ": " + member.dateOfBirth.toString());
            if (member.dateOfDeath != null)
                System.out.println("dod of " + member.relation + ": " + member.dateOfDeath);
            System.out.println();

            if (member.children.size() > 0) {
                for (Person child : member.children) {
                    System.out.println("Name of " + child.relation + ": " + child.name);
                    System.out.println("Dob of " + child.relation + ": " + child.dateOfBirth.toString());
                    if (child.dateOfDeath != null)
                        System.out.println("Dod of " + child.relation + ": " + child.dateOfDeath);
                    System.out.println();
                }
            }
        }
    }
}

public class Main {
    static Scanner scanner = new Scanner(System.in);

    private static Date parseDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd format.");
            return null;
        }
    }

    public static Person getDetails(String relation) {
        System.out.println("Enter the Name of " + relation);
        String sName = scanner.nextLine();
        System.out.println("Enter date of birth (yyyy-MM-dd):");
        Date sdob = parseDate(scanner.nextLine());
        while (sdob == null) {
            System.out.println("Invalid");
            System.out.println("Enter date of birth (yyyy-MM-dd):");
            sdob = parseDate(scanner.nextLine());
        }
        System.out.println("Enter date of death (yyyy-MM-dd):");
        Date sdod = null;
        String temp = scanner.nextLine();
        if (temp.length() > 0)
            sdod = parseDate(temp);

        while (sdob != null && sdod != null && sdod.before(sdob)) {
            System.out.println("Date of death cannot be before date of birth. Please re-enter.");
            System.out.println("Enter date of death (yyyy-MM-dd):");
            sdod = parseDate(scanner.nextLine());
        }
        if (sdob != null) {
            return new Person(sName, sdob, sdod, relation);
        }
        return null;
    }

    public static void will(Deceased deceased) {


        System.out.println("Enter the will amount: ");
        double amount = scanner.nextDouble();
        System.out.println("Choose the system number:");
        System.out.printf("1. English\n2. Scottish\n3. New York State\n4. California\n");
        int system = scanner.nextInt();

        ArrayList<Person> spouse = new ArrayList<>();
        ArrayList<Person> parents = new ArrayList<>();
        ArrayList<Person> gparents = new ArrayList<>();
        ArrayList<Person> uncle_aunt = new ArrayList<>();
        ArrayList<Person> siblings = new ArrayList<>();
        ArrayList<Person> children = new ArrayList<>();

        for (Person member : deceased.familyMembers) {
            if (member.relation.equals("spouse")) spouse.add(member);
            if (member.relation.equals("parent")) parents.add(member);
            if (member.relation.equals("grandparent")) gparents.add(member);
            if (member.relation.equals("uncle/aunt")) uncle_aunt.add(member);
            if (member.relation.equals("sibling")) siblings.add(member);
            if (member.relation.equals("child")) children.add(member);
        }


        switch (system) {
            case 1:
                if (spouse.size() > 0 && deceased.marriages.get(deceased.marriages.size() - 1).divorceDate == null && spouse.get(0).dateOfDeath == null) {
                    if (amount <= 270_000) {
                        System.out.println("The spouse will receive entire estate of " + amount);
                        return;
                    } else {
                        amount -= 270_000;
                        amount *= 0.5;
                        System.out.println("The spouse will receive estate of " + (270_000 + amount));
                        //if children exist
                        if (children.size() > 0) {
                            int aliveCounter = 0;
                            int deadCounter = 0;
                            for (Person child : children) {
                                if (child.dateOfDeath == null)
                                    aliveCounter++;
                                else deadCounter++;
                            }
                            //if all children are alive
                            if (aliveCounter == children.size()) {
                                for (Person child : children) {
                                    System.out.println(child.name + " will receive: " + amount / aliveCounter);
                                }
                                return;
                            }
                            //if all are dead
                            else if (deadCounter == children.size()) {
                                //case: some dead have children some dead dont
                                int haveChildren = 0;
                                for (Person child : children) {
                                    if (child.children.size() > 0) haveChildren++;
                                }
                                if (haveChildren > 0) {
                                    amount = amount / haveChildren;
                                    for (Person child : children) {
                                        if (child.children.size() > 0) {
                                            for (Person gchild : child.children) {
                                                System.out.println(gchild.name + " will receive " + amount / child.children.size());
                                            }
                                        }
                                    }
                                    return;
                                }
                                //case: all dead dont have children - go to parents
                                int aliveParent = 0;
                                for (Person parent : parents) {
                                    if (parent.dateOfDeath == null) aliveParent++;
                                }
                                if (aliveParent > 0) {
                                    amount /= aliveParent;
                                    for (Person parent : parents) {
                                        if (parent.dateOfDeath == null)
                                            System.out.println(parent.name + " will receive " + amount);
                                    }
                                    return;
                                }
                                //none of my parents are alive

                                //if siblings exists
                                if (siblings.size() > 0) {
                                    int aliveSiblingsCounter = 0;
                                    int deadSiblingsCounter = 0;

                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                        else deadSiblingsCounter++;
                                    }
                                    //if all siblings are alive
                                    if (aliveSiblingsCounter == siblings.size()) {
                                        for (Person sibling : siblings) {
                                            System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                        }
                                        return;
                                    }
                                    //if all siblings are dead
                                    else if (deadSiblingsCounter == siblings.size()) {
                                        //some dead have children some dont
                                        int siblingHasChildren = 0;
                                        for (Person sibling : siblings) {
                                            if (sibling.children.size() > 0) siblingHasChildren++;
                                        }
                                        if (siblingHasChildren > 0) {
                                            amount = amount / siblingHasChildren;
                                            for (Person sibling : siblings) {
                                                if (sibling.children.size() > 0) {
                                                    for (Person n : sibling.children) {
                                                        System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                        //if all are dead and none have children - go to grandparents
                                        int aliveGrandParent = 0;
                                        for (Person gparent : gparents) {
                                            if (gparent.dateOfDeath == null) aliveGrandParent++;
                                        }
                                        if (aliveGrandParent > 0) {
                                            amount /= aliveGrandParent;
                                            for (Person gparent : gparents) {
                                                if (gparent.dateOfDeath == null)
                                                    System.out.println(gparent.name + " will receive " + amount);
                                            }
                                            return;
                                        }

                                        // if all grand parents are dead

                                        //if uncles exists

                                        if (uncle_aunt.size() > 0) {
                                            int aliveUncleCounter = 0;
                                            int deadUncleCounter = 0;

                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                                else deadUncleCounter++;
                                            }
                                            //if all uncles are alive
                                            if (aliveUncleCounter == uncle_aunt.size()) {
                                                for (Person uncle : uncle_aunt) {
                                                    System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                                }
                                                return;
                                            }
                                            //if all uncles are dead

                                            if (deadUncleCounter == uncle_aunt.size()) {
                                                //some have children some dont
                                                int uncleHasChildren = 0;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.children.size() > 0) uncleHasChildren++;
                                                }

                                                if (uncleHasChildren > 0) {
                                                    amount = amount / uncleHasChildren;
                                                    for (Person uncle : uncle_aunt) {
                                                        if (uncle.children.size() > 0) {
                                                            for (Person n : uncle.children) {
                                                                System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                            }
                                                        }
                                                    }
                                                    return;
                                                }
                                                //if all are dead and none have children - go to govt
                                                System.out.println("Govt/Crown will receive " + amount);
                                                return;
                                            }

                                            // if some uncles are alive some are dead
                                            else {
                                                int deadUncleHasChildCounter = 0;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                        deadUncleHasChildCounter++;
                                                }
//                                //no dead uncles has children
                                                if (deadUncleHasChildCounter == 0) {
                                                    amount = amount / aliveUncleCounter;
                                                    for (Person uncle : uncle_aunt) {
                                                        if (uncle.dateOfDeath == null)
                                                            System.out.println(uncle.name + " will receive: " + amount);
                                                    }
                                                    return;
                                                }
//                                //some dead uncles have children
                                                else {
                                                    amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                                    for (Person uncle : uncle_aunt) {
                                                        if (uncle.dateOfDeath == null)
                                                            System.out.println(uncle.name + " will receive: " + amount);
                                                        else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                            for (Person gchild : uncle.children) {
                                                                System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                            }
                                                        }
                                                    }
                                                    return;
                                                }

                                            }
                                        }
                                        //if uncles dont exist
                                        else {
                                            System.out.println("The Govt/Crown will receive: " + amount);
                                        }
                                    }
                                    //if some siblings are alive some are dead
                                    else {
                                        int deadSiblingHasChildCounter = 0;
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                                deadSiblingHasChildCounter++;
                                        }
//                                //no dead sibling has children
                                        if (deadSiblingHasChildCounter == 0) {
                                            amount = amount / aliveSiblingsCounter;
                                            for (Person sibling : siblings) {
                                                if (sibling.dateOfDeath == null)
                                                    System.out.println(sibling.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead sibling have children
                                        else {
                                            amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                            for (Person sibling : siblings) {
                                                if (sibling.dateOfDeath == null)
                                                    System.out.println(sibling.name + " will receive: " + amount);
                                                else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                                    for (Person gchild : sibling.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if siblings dont exist
                                else {
                                    int aliveGrandParent = 0;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null) aliveGrandParent++;
                                    }
                                    if (aliveGrandParent > 0) {
                                        amount /= aliveGrandParent;
                                        for (Person gparent : gparents) {
                                            if (gparent.dateOfDeath == null)
                                                System.out.println(gparent.name + " will receive " + amount);
                                        }
                                        return;
                                    }

                                    // if all grand parents are dead

                                    //if uncles exists

                                    if (uncle_aunt.size() > 0) {
                                        int aliveUncleCounter = 0;
                                        int deadUncleCounter = 0;

                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                            else deadUncleCounter++;
                                        }
                                        //if all uncles are alive
                                        if (aliveUncleCounter == uncle_aunt.size()) {
                                            for (Person uncle : uncle_aunt) {
                                                System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                            }
                                            return;
                                        }
                                        //if all uncles are dead

                                        if (deadUncleCounter == uncle_aunt.size()) {
                                            //some have children some dont
                                            int uncleHasChildren = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) uncleHasChildren++;
                                            }

                                            if (uncleHasChildren > 0) {
                                                amount = amount / uncleHasChildren;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.children.size() > 0) {
                                                        for (Person n : uncle.children) {
                                                            System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                            //if all are dead and none have children - go to govt
                                            System.out.println("Govt/Crown will receive " + amount);
                                            return;
                                        }

                                        // if some uncles are alive some are dead
                                        else {
                                            int deadUncleHasChildCounter = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                    deadUncleHasChildCounter++;
                                            }
//                                //no dead uncles has children
                                            if (deadUncleHasChildCounter == 0) {
                                                amount = amount / aliveUncleCounter;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                }
                                                return;
                                            }
//                                //some dead uncles have children
                                            else {
                                                amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                    else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                        for (Person gchild : uncle.children) {
                                                            System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                        }
                                                    }
                                                }
                                                return;
                                            }

                                        }
                                    }
                                    //if uncles dont exist
                                    else {
                                        System.out.println("The Govt/Crown will receive: " + amount);
                                    }
                                }

                            }
                            //some children are alive some are dead
                            else {
                                int deadHasChildCounter = 0;
                                for (Person child : children) {
                                    if (child.dateOfDeath != null && child.children.size() > 0)
                                        deadHasChildCounter++;
                                }
//                                //no dead has children
                                if (deadHasChildCounter == 0) {
                                    amount = amount / aliveCounter;
                                    for (Person child : children) {
                                        if (child.dateOfDeath == null)
                                            System.out.println(child.name + " will receive: " + amount);
                                    }
                                    return;
                                }
//                                //some dead have children
                                else {
                                    amount = amount / (aliveCounter + deadHasChildCounter);
                                    for (Person child : children) {
                                        if (child.dateOfDeath == null)
                                            System.out.println(child.name + " will receive: " + amount);
                                        else if (child.dateOfDeath != null && child.children.size() > 0) {
                                            for (Person gchild : child.children) {
                                                System.out.println(gchild.name + " will receive: " + (amount / (child.children.size())));
                                            }
                                        }
                                    }
                                    return;
                                }

                            }
                        }
                        //if children dont exist
                        else {
                            int aliveParent = 0;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null) aliveParent++;
                            }
                            if (aliveParent > 0) {
                                amount /= aliveParent;
                                for (Person parent : parents) {
                                    if (parent.dateOfDeath == null)
                                        System.out.println(parent.name + " will receive " + amount);
                                }
                                return;
                            }
                            //none of my parents are alive

                            //if siblings exists
                            if (siblings.size() > 0) {
                                int aliveSiblingsCounter = 0;
                                int deadSiblingsCounter = 0;

                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                    else deadSiblingsCounter++;
                                }
                                //if all siblings are alive
                                if (aliveSiblingsCounter == siblings.size()) {
                                    for (Person sibling : siblings) {
                                        System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                    }
                                    return;
                                }
                                //if all siblings are dead
                                else if (deadSiblingsCounter == siblings.size()) {
                                    //some dead have children some dont
                                    int siblingHasChildren = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) siblingHasChildren++;
                                    }
                                    if (siblingHasChildren > 0) {
                                        amount = amount / siblingHasChildren;
                                        for (Person sibling : siblings) {
                                            if (sibling.children.size() > 0) {
                                                for (Person n : sibling.children) {
                                                    System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }

                                    //if all are dead and none have children - go to grandparents
                                    int aliveGrandParent = 0;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null) aliveGrandParent++;
                                    }
                                    if (aliveGrandParent > 0) {
                                        amount /= aliveGrandParent;
                                        for (Person gparent : gparents) {
                                            if (gparent.dateOfDeath == null)
                                                System.out.println(gparent.name + " will receive " + amount);
                                        }
                                        return;
                                    }

                                    // if all grand parents are dead

                                    //if uncles exists

                                    if (uncle_aunt.size() > 0) {
                                        int aliveUncleCounter = 0;
                                        int deadUncleCounter = 0;

                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                            else deadUncleCounter++;
                                        }
                                        //if all uncles are alive
                                        if (aliveUncleCounter == uncle_aunt.size()) {
                                            for (Person uncle : uncle_aunt) {
                                                System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                            }
                                            return;
                                        }
                                        //if all uncles are dead

                                        if (deadUncleCounter == uncle_aunt.size()) {
                                            //some have children some dont
                                            int uncleHasChildren = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) uncleHasChildren++;
                                            }

                                            if (uncleHasChildren > 0) {
                                                amount = amount / uncleHasChildren;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.children.size() > 0) {
                                                        for (Person n : uncle.children) {
                                                            System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                            //if all are dead and none have children - go to govt
                                            System.out.println("Govt/Crown will receive " + amount);
                                            return;
                                        }

                                        // if some uncles are alive some are dead
                                        else {
                                            int deadUncleHasChildCounter = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                    deadUncleHasChildCounter++;
                                            }
//                                //no dead uncles has children
                                            if (deadUncleHasChildCounter == 0) {
                                                amount = amount / aliveUncleCounter;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                }
                                                return;
                                            }
//                                //some dead uncles have children
                                            else {
                                                amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                    else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                        for (Person gchild : uncle.children) {
                                                            System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                        }
                                                    }
                                                }
                                                return;
                                            }

                                        }
                                    }
                                    //if uncles dont exist
                                    else {
                                        System.out.println("The Govt/Crown will receive: " + amount);
                                    }
                                }
                                //if some siblings are alive some are dead
                                else {
                                    int deadSiblingHasChildCounter = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                            deadSiblingHasChildCounter++;
                                    }
//                                //no dead sibling has children
                                    if (deadSiblingHasChildCounter == 0) {
                                        amount = amount / aliveSiblingsCounter;
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead sibling have children
                                    else {
                                        amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                            else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                                for (Person gchild : sibling.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }

                                }
                            }
                            //if siblings dont exist
                            else {
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }
                        }
                    }
                } else {
                    //if children exist
                    if (children.size() > 0) {
                        int aliveCounter = 0;
                        int deadCounter = 0;
                        for (Person child : children) {
                            if (child.dateOfDeath == null)
                                aliveCounter++;
                            else deadCounter++;
                        }
                        //if all children are alive
                        if (aliveCounter == children.size()) {
                            for (Person child : children) {
                                System.out.println(child.name + " will receive: " + amount / aliveCounter);
                            }
                            return;
                        }
                        //if all are dead
                        else if (deadCounter == children.size()) {
                            //case: some dead have children some dead dont
                            int haveChildren = 0;
                            for (Person child : children) {
                                if (child.children.size() > 0) haveChildren++;
                            }
                            if (haveChildren > 0) {
                                amount = amount / haveChildren;
                                for (Person child : children) {
                                    if (child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive " + amount / child.children.size());
                                        }
                                    }
                                }
                                return;
                            }
                            //case: all dead dont have children - go to parents
                            int aliveParent = 0;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null) aliveParent++;
                            }
                            if (aliveParent > 0) {
                                amount /= aliveParent;
                                for (Person parent : parents) {
                                    if (parent.dateOfDeath == null)
                                        System.out.println(parent.name + " will receive " + amount);
                                }
                                return;
                            }
                            //none of my parents are alive

                            //if siblings exists
                            if (siblings.size() > 0) {
                                int aliveSiblingsCounter = 0;
                                int deadSiblingsCounter = 0;

                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                    else deadSiblingsCounter++;
                                }
                                //if all siblings are alive
                                if (aliveSiblingsCounter == siblings.size()) {
                                    for (Person sibling : siblings) {
                                        System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                    }
                                    return;
                                }
                                //if all siblings are dead
                                else if (deadSiblingsCounter == siblings.size()) {
                                    //some dead have children some dont
                                    int siblingHasChildren = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) siblingHasChildren++;
                                    }
                                    if (siblingHasChildren > 0) {
                                        amount = amount / siblingHasChildren;
                                        for (Person sibling : siblings) {
                                            if (sibling.children.size() > 0) {
                                                for (Person n : sibling.children) {
                                                    System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }

                                    //if all are dead and none have children - go to grandparents
                                    int aliveGrandParent = 0;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null) aliveGrandParent++;
                                    }
                                    if (aliveGrandParent > 0) {
                                        amount /= aliveGrandParent;
                                        for (Person gparent : gparents) {
                                            if (gparent.dateOfDeath == null)
                                                System.out.println(gparent.name + " will receive " + amount);
                                        }
                                        return;
                                    }

                                    // if all grand parents are dead

                                    //if uncles exists

                                    if (uncle_aunt.size() > 0) {
                                        int aliveUncleCounter = 0;
                                        int deadUncleCounter = 0;

                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                            else deadUncleCounter++;
                                        }
                                        //if all uncles are alive
                                        if (aliveUncleCounter == uncle_aunt.size()) {
                                            for (Person uncle : uncle_aunt) {
                                                System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                            }
                                            return;
                                        }
                                        //if all uncles are dead

                                        if (deadUncleCounter == uncle_aunt.size()) {
                                            //some have children some dont
                                            int uncleHasChildren = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) uncleHasChildren++;
                                            }

                                            if (uncleHasChildren > 0) {
                                                amount = amount / uncleHasChildren;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.children.size() > 0) {
                                                        for (Person n : uncle.children) {
                                                            System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                            //if all are dead and none have children - go to govt
                                            System.out.println("Govt/Crown will receive " + amount);
                                            return;
                                        }

                                        // if some uncles are alive some are dead
                                        else {
                                            int deadUncleHasChildCounter = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                    deadUncleHasChildCounter++;
                                            }
//                                //no dead uncles has children
                                            if (deadUncleHasChildCounter == 0) {
                                                amount = amount / aliveUncleCounter;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                }
                                                return;
                                            }
//                                //some dead uncles have children
                                            else {
                                                amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                    else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                        for (Person gchild : uncle.children) {
                                                            System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                        }
                                                    }
                                                }
                                                return;
                                            }

                                        }
                                    }
                                    //if uncles dont exist
                                    else {
                                        System.out.println("The Govt/Crown will receive: " + amount);
                                    }
                                }
                                //if some siblings are alive some are dead
                                else {
                                    int deadSiblingHasChildCounter = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                            deadSiblingHasChildCounter++;
                                    }
//                                //no dead sibling has children
                                    if (deadSiblingHasChildCounter == 0) {
                                        amount = amount / aliveSiblingsCounter;
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead sibling have children
                                    else {
                                        amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                            else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                                for (Person gchild : sibling.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }

                                }
                            }
                            //if siblings dont exist
                            else {
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }

                        }
                        //some children are alive some are dead
                        else {
                            int deadHasChildCounter = 0;
                            for (Person child : children) {
                                if (child.dateOfDeath != null && child.children.size() > 0)
                                    deadHasChildCounter++;
                            }
//                                //no dead has children
                            if (deadHasChildCounter == 0) {
                                amount = amount / aliveCounter;
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                }
                                return;
                            }
//                                //some dead have children
                            else {
                                amount = amount / (aliveCounter + deadHasChildCounter);
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                    else if (child.dateOfDeath != null && child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive: " + (amount / (child.children.size())));
                                        }
                                    }
                                }
                                return;
                            }

                        }
                    }
                    //if children dont exist
                    else {
                        int aliveParent = 0;
                        for (Person parent : parents) {
                            if (parent.dateOfDeath == null) aliveParent++;
                        }
                        if (aliveParent > 0) {
                            amount /= aliveParent;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null)
                                    System.out.println(parent.name + " will receive " + amount);
                            }
                            return;
                        }
                        //none of my parents are alive

                        //if siblings exists
                        if (siblings.size() > 0) {
                            int aliveSiblingsCounter = 0;
                            int deadSiblingsCounter = 0;

                            for (Person sibling : siblings) {
                                if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                else deadSiblingsCounter++;
                            }
                            //if all siblings are alive
                            if (aliveSiblingsCounter == siblings.size()) {
                                for (Person sibling : siblings) {
                                    System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                }
                                return;
                            }
                            //if all siblings are dead
                            else if (deadSiblingsCounter == siblings.size()) {
                                //some dead have children some dont
                                int siblingHasChildren = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.children.size() > 0) siblingHasChildren++;
                                }
                                if (siblingHasChildren > 0) {
                                    amount = amount / siblingHasChildren;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) {
                                            for (Person n : sibling.children) {
                                                System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                            }
                                        }
                                    }
                                    return;
                                }

                                //if all are dead and none have children - go to grandparents
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }
                            //if some siblings are alive some are dead
                            else {
                                int deadSiblingHasChildCounter = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                        deadSiblingHasChildCounter++;
                                }
//                                //no dead sibling has children
                                if (deadSiblingHasChildCounter == 0) {
                                    amount = amount / aliveSiblingsCounter;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                    }
                                    return;
                                }
//                                //some dead sibling have children
                                else {
                                    amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                        else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                            for (Person gchild : sibling.children) {
                                                System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                            }
                                        }
                                    }
                                    return;
                                }

                            }
                        }
                        //if siblings dont exist
                        else {
                            int aliveGrandParent = 0;
                            for (Person gparent : gparents) {
                                if (gparent.dateOfDeath == null) aliveGrandParent++;
                            }
                            if (aliveGrandParent > 0) {
                                amount /= aliveGrandParent;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null)
                                        System.out.println(gparent.name + " will receive " + amount);
                                }
                                return;
                            }

                            // if all grand parents are dead

                            //if uncles exists

                            if (uncle_aunt.size() > 0) {
                                int aliveUncleCounter = 0;
                                int deadUncleCounter = 0;

                                for (Person uncle : uncle_aunt) {
                                    if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                    else deadUncleCounter++;
                                }
                                //if all uncles are alive
                                if (aliveUncleCounter == uncle_aunt.size()) {
                                    for (Person uncle : uncle_aunt) {
                                        System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                    }
                                    return;
                                }
                                //if all uncles are dead

                                if (deadUncleCounter == uncle_aunt.size()) {
                                    //some have children some dont
                                    int uncleHasChildren = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.children.size() > 0) uncleHasChildren++;
                                    }

                                    if (uncleHasChildren > 0) {
                                        amount = amount / uncleHasChildren;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) {
                                                for (Person n : uncle.children) {
                                                    System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    //if all are dead and none have children - go to govt
                                    System.out.println("Govt/Crown will receive " + amount);
                                    return;
                                }

                                // if some uncles are alive some are dead
                                else {
                                    int deadUncleHasChildCounter = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                            deadUncleHasChildCounter++;
                                    }
//                                //no dead uncles has children
                                    if (deadUncleHasChildCounter == 0) {
                                        amount = amount / aliveUncleCounter;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead uncles have children
                                    else {
                                        amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                            else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                for (Person gchild : uncle.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }
                                }
                            }
                            //if uncles dont exist
                            else {
                                System.out.println("The Govt/Crown will receive: " + amount);
                            }
                        }
                    }
                }
                break;
            case 2:
                if (spouse.size() > 0 && deceased.marriages.get(deceased.marriages.size() - 1).divorceDate == null && spouse.get(0).dateOfDeath == null) {
                    int aliveChildC = 0;
                    for (Person child : children) {
                        if (child.dateOfDeath == null) aliveChildC++;
                    }
                    if (aliveChildC == 0) {
                        System.out.println("The spouse will receive entire estate of " + amount);
                        return;
                    } else {
                        System.out.println("Enter the Legal Rights Value");
                        double legalRights = scanner.nextDouble();
                        amount -= legalRights;
                        System.out.println("Enter the prior rights value for children");
                        double priorRights = scanner.nextDouble();
                        amount -= (priorRights);
                        System.out.println("The spouse will receive legal right of " + legalRights + " part of estate: " + amount);
                        priorRights /= aliveChildC;

                        for (Person child : children) {
                            if (child.dateOfDeath == null) {
                                System.out.println(child.name + " will receive: " + priorRights);
                            }
                        }
                        return;
                    }
                } else {
                    //if children exist
                    if (children.size() > 0) {
                        int aliveCounter = 0;
                        int deadCounter = 0;
                        for (Person child : children) {
                            if (child.dateOfDeath == null)
                                aliveCounter++;
                            else deadCounter++;
                        }
                        //if all children are alive
                        if (aliveCounter == children.size()) {
                            for (Person child : children) {
                                System.out.println(child.name + " will receive: " + amount / aliveCounter);
                            }
                            return;
                        }
                        //if all are dead
                        else if (deadCounter == children.size()) {
                            //case: some dead have children some dead dont
                            int haveChildren = 0;
                            for (Person child : children) {
                                if (child.children.size() > 0) haveChildren++;
                            }
                            if (haveChildren > 0) {
                                amount = amount / haveChildren;
                                for (Person child : children) {
                                    if (child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive " + amount / child.children.size());
                                        }
                                    }
                                }
                                return;
                            }
                            //case: all dead dont have children - go to parents
                            int aliveParent = 0;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null) aliveParent++;
                            }
                            if (aliveParent > 0) {
                                amount /= aliveParent;
                                for (Person parent : parents) {
                                    if (parent.dateOfDeath == null)
                                        System.out.println(parent.name + " will receive " + amount);
                                }
                                return;
                            }
                            //none of my parents are alive

                            //if siblings exists
                            if (siblings.size() > 0) {
                                int aliveSiblingsCounter = 0;
                                int deadSiblingsCounter = 0;

                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                    else deadSiblingsCounter++;
                                }
                                //if all siblings are alive
                                if (aliveSiblingsCounter == siblings.size()) {
                                    for (Person sibling : siblings) {
                                        System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                    }
                                    return;
                                }
                                //if all siblings are dead
                                else if (deadSiblingsCounter == siblings.size()) {
                                    //some dead have children some dont
                                    int siblingHasChildren = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) siblingHasChildren++;
                                    }
                                    if (siblingHasChildren > 0) {
                                        amount = amount / siblingHasChildren;
                                        for (Person sibling : siblings) {
                                            if (sibling.children.size() > 0) {
                                                for (Person n : sibling.children) {
                                                    System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }

                                    //if all are dead and none have children - go to grandparents
                                    int aliveGrandParent = 0;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null) aliveGrandParent++;
                                    }
                                    if (aliveGrandParent > 0) {
                                        amount /= aliveGrandParent;
                                        for (Person gparent : gparents) {
                                            if (gparent.dateOfDeath == null)
                                                System.out.println(gparent.name + " will receive " + amount);
                                        }
                                        return;
                                    }

                                    // if all grand parents are dead

                                    //if uncles exists

                                    if (uncle_aunt.size() > 0) {
                                        int aliveUncleCounter = 0;
                                        int deadUncleCounter = 0;

                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                            else deadUncleCounter++;
                                        }
                                        //if all uncles are alive
                                        if (aliveUncleCounter == uncle_aunt.size()) {
                                            for (Person uncle : uncle_aunt) {
                                                System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                            }
                                            return;
                                        }
                                        //if all uncles are dead

                                        if (deadUncleCounter == uncle_aunt.size()) {
                                            //some have children some dont
                                            int uncleHasChildren = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) uncleHasChildren++;
                                            }

                                            if (uncleHasChildren > 0) {
                                                amount = amount / uncleHasChildren;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.children.size() > 0) {
                                                        for (Person n : uncle.children) {
                                                            System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                            //if all are dead and none have children - go to govt
                                            System.out.println("Govt/Crown will receive " + amount);
                                            return;
                                        }

                                        // if some uncles are alive some are dead
                                        else {
                                            int deadUncleHasChildCounter = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                    deadUncleHasChildCounter++;
                                            }
//                                //no dead uncles has children
                                            if (deadUncleHasChildCounter == 0) {
                                                amount = amount / aliveUncleCounter;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                }
                                                return;
                                            }
//                                //some dead uncles have children
                                            else {
                                                amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                    else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                        for (Person gchild : uncle.children) {
                                                            System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                        }
                                                    }
                                                }
                                                return;
                                            }

                                        }
                                    }
                                    //if uncles dont exist
                                    else {
                                        System.out.println("The Govt/Crown will receive: " + amount);
                                    }
                                }
                                //if some siblings are alive some are dead
                                else {
                                    int deadSiblingHasChildCounter = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                            deadSiblingHasChildCounter++;
                                    }
//                                //no dead sibling has children
                                    if (deadSiblingHasChildCounter == 0) {
                                        amount = amount / aliveSiblingsCounter;
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead sibling have children
                                    else {
                                        amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                            else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                                for (Person gchild : sibling.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }

                                }
                            }
                            //if siblings dont exist
                            else {
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }

                        }
                        //some children are alive some are dead
                        else {
                            int deadHasChildCounter = 0;
                            for (Person child : children) {
                                if (child.dateOfDeath != null && child.children.size() > 0)
                                    deadHasChildCounter++;
                            }
//                                //no dead has children
                            if (deadHasChildCounter == 0) {
                                amount = amount / aliveCounter;
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                }
                                return;
                            }
//                                //some dead have children
                            else {
                                amount = amount / (aliveCounter + deadHasChildCounter);
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                    else if (child.dateOfDeath != null && child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive: " + (amount / (child.children.size())));
                                        }
                                    }
                                }
                                return;
                            }

                        }
                    }
                    //if children dont exist
                    else {
                        int aliveParent = 0;
                        for (Person parent : parents) {
                            if (parent.dateOfDeath == null) aliveParent++;
                        }
                        if (aliveParent > 0) {
                            amount /= aliveParent;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null)
                                    System.out.println(parent.name + " will receive " + amount);
                            }
                            return;
                        }
                        //none of my parents are alive

                        //if siblings exists
                        if (siblings.size() > 0) {
                            int aliveSiblingsCounter = 0;
                            int deadSiblingsCounter = 0;

                            for (Person sibling : siblings) {
                                if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                else deadSiblingsCounter++;
                            }
                            //if all siblings are alive
                            if (aliveSiblingsCounter == siblings.size()) {
                                for (Person sibling : siblings) {
                                    System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                }
                                return;
                            }
                            //if all siblings are dead
                            else if (deadSiblingsCounter == siblings.size()) {
                                //some dead have children some dont
                                int siblingHasChildren = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.children.size() > 0) siblingHasChildren++;
                                }
                                if (siblingHasChildren > 0) {
                                    amount = amount / siblingHasChildren;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) {
                                            for (Person n : sibling.children) {
                                                System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                            }
                                        }
                                    }
                                    return;
                                }

                                //if all are dead and none have children - go to grandparents
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }
                            //if some siblings are alive some are dead
                            else {
                                int deadSiblingHasChildCounter = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                        deadSiblingHasChildCounter++;
                                }
//                                //no dead sibling has children
                                if (deadSiblingHasChildCounter == 0) {
                                    amount = amount / aliveSiblingsCounter;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                    }
                                    return;
                                }
//                                //some dead sibling have children
                                else {
                                    amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                        else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                            for (Person gchild : sibling.children) {
                                                System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                            }
                                        }
                                    }
                                    return;
                                }

                            }
                        }
                        //if siblings dont exist
                        else {
                            int aliveGrandParent = 0;
                            for (Person gparent : gparents) {
                                if (gparent.dateOfDeath == null) aliveGrandParent++;
                            }
                            if (aliveGrandParent > 0) {
                                amount /= aliveGrandParent;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null)
                                        System.out.println(gparent.name + " will receive " + amount);
                                }
                                return;
                            }

                            // if all grand parents are dead

                            //if uncles exists

                            if (uncle_aunt.size() > 0) {
                                int aliveUncleCounter = 0;
                                int deadUncleCounter = 0;

                                for (Person uncle : uncle_aunt) {
                                    if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                    else deadUncleCounter++;
                                }
                                //if all uncles are alive
                                if (aliveUncleCounter == uncle_aunt.size()) {
                                    for (Person uncle : uncle_aunt) {
                                        System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                    }
                                    return;
                                }
                                //if all uncles are dead

                                if (deadUncleCounter == uncle_aunt.size()) {
                                    //some have children some dont
                                    int uncleHasChildren = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.children.size() > 0) uncleHasChildren++;
                                    }

                                    if (uncleHasChildren > 0) {
                                        amount = amount / uncleHasChildren;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) {
                                                for (Person n : uncle.children) {
                                                    System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    //if all are dead and none have children - go to govt
                                    System.out.println("Govt/Crown will receive " + amount);
                                    return;
                                }

                                // if some uncles are alive some are dead
                                else {
                                    int deadUncleHasChildCounter = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                            deadUncleHasChildCounter++;
                                    }
//                                //no dead uncles has children
                                    if (deadUncleHasChildCounter == 0) {
                                        amount = amount / aliveUncleCounter;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead uncles have children
                                    else {
                                        amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                            else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                for (Person gchild : uncle.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }
                                }
                            }
                            //if uncles dont exist
                            else {
                                System.out.println("The Govt/Crown will receive: " + amount);
                            }
                        }
                    }
                }
                break;
            case 3:
                if (spouse.size() > 0 && deceased.marriages.get(deceased.marriages.size() - 1).divorceDate == null && spouse.get(0).dateOfDeath == null) {
                    if (amount <= 50_000) {
                        System.out.println("The spouse will receive entire estate of " + amount);
                        return;
                    } else {
                        int aliveChildC = 0;
                        for (Person child : children) {
                            if (child.dateOfDeath == null) aliveChildC++;
                        }
                        if (aliveChildC == 0) {
                            System.out.println("The spouse will receive entire estate of " + amount);
                            return;
                        } else {
                            amount -= 50_000;
                            amount *= 0.5;
                            System.out.println("The spouse will receive estate of " + (270_000 + amount));
                            amount /= aliveChildC;

                            for (Person child : children) {
                                if (child.dateOfDeath == null) {
                                    System.out.println(child.name + " will receive: " + amount);
                                }
                            }
                            return;
                        }
                    }
                } else {
                    //if children exist
                    if (children.size() > 0) {
                        int aliveCounter = 0;
                        int deadCounter = 0;
                        for (Person child : children) {
                            if (child.dateOfDeath == null)
                                aliveCounter++;
                            else deadCounter++;
                        }
                        //if all children are alive
                        if (aliveCounter == children.size()) {
                            for (Person child : children) {
                                System.out.println(child.name + " will receive: " + amount / aliveCounter);
                            }
                            return;
                        }
                        //if all are dead
                        else if (deadCounter == children.size()) {
                            //case: some dead have children some dead dont
                            int haveChildren = 0;
                            for (Person child : children) {
                                if (child.children.size() > 0) haveChildren++;
                            }
                            if (haveChildren > 0) {
                                amount = amount / haveChildren;
                                for (Person child : children) {
                                    if (child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive " + amount / child.children.size());
                                        }
                                    }
                                }
                                return;
                            }
                            //case: all dead dont have children - go to parents
                            int aliveParent = 0;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null) aliveParent++;
                            }
                            if (aliveParent > 0) {
                                amount /= aliveParent;
                                for (Person parent : parents) {
                                    if (parent.dateOfDeath == null)
                                        System.out.println(parent.name + " will receive " + amount);
                                }
                                return;
                            }
                            //none of my parents are alive

                            //if siblings exists
                            if (siblings.size() > 0) {
                                int aliveSiblingsCounter = 0;
                                int deadSiblingsCounter = 0;

                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                    else deadSiblingsCounter++;
                                }
                                //if all siblings are alive
                                if (aliveSiblingsCounter == siblings.size()) {
                                    for (Person sibling : siblings) {
                                        System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                    }
                                    return;
                                }
                                //if all siblings are dead
                                else if (deadSiblingsCounter == siblings.size()) {
                                    //some dead have children some dont
                                    int siblingHasChildren = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) siblingHasChildren++;
                                    }
                                    if (siblingHasChildren > 0) {
                                        amount = amount / siblingHasChildren;
                                        for (Person sibling : siblings) {
                                            if (sibling.children.size() > 0) {
                                                for (Person n : sibling.children) {
                                                    System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }

                                    //if all are dead and none have children - go to grandparents
                                    int aliveGrandParent = 0;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null) aliveGrandParent++;
                                    }
                                    if (aliveGrandParent > 0) {
                                        amount /= aliveGrandParent;
                                        for (Person gparent : gparents) {
                                            if (gparent.dateOfDeath == null)
                                                System.out.println(gparent.name + " will receive " + amount);
                                        }
                                        return;
                                    }

                                    // if all grand parents are dead

                                    //if uncles exists

                                    if (uncle_aunt.size() > 0) {
                                        int aliveUncleCounter = 0;
                                        int deadUncleCounter = 0;

                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                            else deadUncleCounter++;
                                        }
                                        //if all uncles are alive
                                        if (aliveUncleCounter == uncle_aunt.size()) {
                                            for (Person uncle : uncle_aunt) {
                                                System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                            }
                                            return;
                                        }
                                        //if all uncles are dead

                                        if (deadUncleCounter == uncle_aunt.size()) {
                                            //some have children some dont
                                            int uncleHasChildren = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) uncleHasChildren++;
                                            }

                                            if (uncleHasChildren > 0) {
                                                amount = amount / uncleHasChildren;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.children.size() > 0) {
                                                        for (Person n : uncle.children) {
                                                            System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                            //if all are dead and none have children - go to govt
                                            System.out.println("Govt/Crown will receive " + amount);
                                            return;
                                        }

                                        // if some uncles are alive some are dead
                                        else {
                                            int deadUncleHasChildCounter = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                    deadUncleHasChildCounter++;
                                            }
//                                //no dead uncles has children
                                            if (deadUncleHasChildCounter == 0) {
                                                amount = amount / aliveUncleCounter;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                }
                                                return;
                                            }
//                                //some dead uncles have children
                                            else {
                                                amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                    else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                        for (Person gchild : uncle.children) {
                                                            System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                        }
                                                    }
                                                }
                                                return;
                                            }

                                        }
                                    }
                                    //if uncles dont exist
                                    else {
                                        System.out.println("The Govt/Crown will receive: " + amount);
                                    }
                                }
                                //if some siblings are alive some are dead
                                else {
                                    int deadSiblingHasChildCounter = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                            deadSiblingHasChildCounter++;
                                    }
//                                //no dead sibling has children
                                    if (deadSiblingHasChildCounter == 0) {
                                        amount = amount / aliveSiblingsCounter;
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead sibling have children
                                    else {
                                        amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                            else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                                for (Person gchild : sibling.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }

                                }
                            }
                            //if siblings dont exist
                            else {
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }

                        }
                        //some children are alive some are dead
                        else {
                            int deadHasChildCounter = 0;
                            for (Person child : children) {
                                if (child.dateOfDeath != null && child.children.size() > 0)
                                    deadHasChildCounter++;
                            }
//                                //no dead has children
                            if (deadHasChildCounter == 0) {
                                amount = amount / aliveCounter;
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                }
                                return;
                            }
//                                //some dead have children
                            else {
                                amount = amount / (aliveCounter + deadHasChildCounter);
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                    else if (child.dateOfDeath != null && child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive: " + (amount / (child.children.size())));
                                        }
                                    }
                                }
                                return;
                            }

                        }
                    }
                    //if children dont exist
                    else {
                        int aliveParent = 0;
                        for (Person parent : parents) {
                            if (parent.dateOfDeath == null) aliveParent++;
                        }
                        if (aliveParent > 0) {
                            amount /= aliveParent;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null)
                                    System.out.println(parent.name + " will receive " + amount);
                            }
                            return;
                        }
                        //none of my parents are alive

                        //if siblings exists
                        if (siblings.size() > 0) {
                            int aliveSiblingsCounter = 0;
                            int deadSiblingsCounter = 0;

                            for (Person sibling : siblings) {
                                if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                else deadSiblingsCounter++;
                            }
                            //if all siblings are alive
                            if (aliveSiblingsCounter == siblings.size()) {
                                for (Person sibling : siblings) {
                                    System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                }
                                return;
                            }
                            //if all siblings are dead
                            else if (deadSiblingsCounter == siblings.size()) {
                                //some dead have children some dont
                                int siblingHasChildren = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.children.size() > 0) siblingHasChildren++;
                                }
                                if (siblingHasChildren > 0) {
                                    amount = amount / siblingHasChildren;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) {
                                            for (Person n : sibling.children) {
                                                System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                            }
                                        }
                                    }
                                    return;
                                }

                                //if all are dead and none have children - go to grandparents
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }
                            //if some siblings are alive some are dead
                            else {
                                int deadSiblingHasChildCounter = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                        deadSiblingHasChildCounter++;
                                }
//                                //no dead sibling has children
                                if (deadSiblingHasChildCounter == 0) {
                                    amount = amount / aliveSiblingsCounter;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                    }
                                    return;
                                }
//                                //some dead sibling have children
                                else {
                                    amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                        else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                            for (Person gchild : sibling.children) {
                                                System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                            }
                                        }
                                    }
                                    return;
                                }

                            }
                        }
                        //if siblings dont exist
                        else {
                            int aliveGrandParent = 0;
                            for (Person gparent : gparents) {
                                if (gparent.dateOfDeath == null) aliveGrandParent++;
                            }
                            if (aliveGrandParent > 0) {
                                amount /= aliveGrandParent;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null)
                                        System.out.println(gparent.name + " will receive " + amount);
                                }
                                return;
                            }

                            // if all grand parents are dead

                            //if uncles exists

                            if (uncle_aunt.size() > 0) {
                                int aliveUncleCounter = 0;
                                int deadUncleCounter = 0;

                                for (Person uncle : uncle_aunt) {
                                    if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                    else deadUncleCounter++;
                                }
                                //if all uncles are alive
                                if (aliveUncleCounter == uncle_aunt.size()) {
                                    for (Person uncle : uncle_aunt) {
                                        System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                    }
                                    return;
                                }
                                //if all uncles are dead

                                if (deadUncleCounter == uncle_aunt.size()) {
                                    //some have children some dont
                                    int uncleHasChildren = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.children.size() > 0) uncleHasChildren++;
                                    }

                                    if (uncleHasChildren > 0) {
                                        amount = amount / uncleHasChildren;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) {
                                                for (Person n : uncle.children) {
                                                    System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    //if all are dead and none have children - go to govt
                                    System.out.println("Govt/Crown will receive " + amount);
                                    return;
                                }

                                // if some uncles are alive some are dead
                                else {
                                    int deadUncleHasChildCounter = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                            deadUncleHasChildCounter++;
                                    }
//                                //no dead uncles has children
                                    if (deadUncleHasChildCounter == 0) {
                                        amount = amount / aliveUncleCounter;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead uncles have children
                                    else {
                                        amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                            else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                for (Person gchild : uncle.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }
                                }
                            }
                            //if uncles dont exist
                            else {
                                System.out.println("The Govt/Crown will receive: " + amount);
                            }
                        }
                    }
                }
                break;
            case 4:
                if (spouse.size() > 0 && deceased.marriages.get(deceased.marriages.size() - 1).divorceDate == null && spouse.get(0).dateOfDeath == null) {
                    int aliveChildC = 0;
                    for (Person child : children) {
                        if (child.dateOfDeath == null) aliveChildC++;
                    }
                    if (aliveChildC == 0) {
                        System.out.println("The spouse will receive entire estate of " + amount);
                        return;
                    } else {
                        System.out.println("Enter the Community Property Value");
                        double communityProperty = scanner.nextDouble();
                        amount -= communityProperty;
                        System.out.println("Enter the remaining property percentage");
                        double remainingPercent = scanner.nextDouble();
                        amount *= (remainingPercent / 100);
                        System.out.println("The spouse will receive community of " + communityProperty + " part of estate: " + amount);
                        amount /= aliveChildC;

                        for (Person child : children) {
                            if (child.dateOfDeath == null) {
                                System.out.println(child.name + " will receive: " + amount);
                            }
                        }
                        return;
                    }
                } else {
                    //if children exist
                    if (children.size() > 0) {
                        int aliveCounter = 0;
                        int deadCounter = 0;
                        for (Person child : children) {
                            if (child.dateOfDeath == null)
                                aliveCounter++;
                            else deadCounter++;
                        }
                        //if all children are alive
                        if (aliveCounter == children.size()) {
                            for (Person child : children) {
                                System.out.println(child.name + " will receive: " + amount / aliveCounter);
                            }
                            return;
                        }
                        //if all are dead
                        else if (deadCounter == children.size()) {
                            //case: some dead have children some dead dont
                            int haveChildren = 0;
                            for (Person child : children) {
                                if (child.children.size() > 0) haveChildren++;
                            }
                            if (haveChildren > 0) {
                                amount = amount / haveChildren;
                                for (Person child : children) {
                                    if (child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive " + amount / child.children.size());
                                        }
                                    }
                                }
                                return;
                            }
                            //case: all dead dont have children - go to parents
                            int aliveParent = 0;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null) aliveParent++;
                            }
                            if (aliveParent > 0) {
                                amount /= aliveParent;
                                for (Person parent : parents) {
                                    if (parent.dateOfDeath == null)
                                        System.out.println(parent.name + " will receive " + amount);
                                }
                                return;
                            }
                            //none of my parents are alive

                            //if siblings exists
                            if (siblings.size() > 0) {
                                int aliveSiblingsCounter = 0;
                                int deadSiblingsCounter = 0;

                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                    else deadSiblingsCounter++;
                                }
                                //if all siblings are alive
                                if (aliveSiblingsCounter == siblings.size()) {
                                    for (Person sibling : siblings) {
                                        System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                    }
                                    return;
                                }
                                //if all siblings are dead
                                else if (deadSiblingsCounter == siblings.size()) {
                                    //some dead have children some dont
                                    int siblingHasChildren = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) siblingHasChildren++;
                                    }
                                    if (siblingHasChildren > 0) {
                                        amount = amount / siblingHasChildren;
                                        for (Person sibling : siblings) {
                                            if (sibling.children.size() > 0) {
                                                for (Person n : sibling.children) {
                                                    System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }

                                    //if all are dead and none have children - go to grandparents
                                    int aliveGrandParent = 0;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null) aliveGrandParent++;
                                    }
                                    if (aliveGrandParent > 0) {
                                        amount /= aliveGrandParent;
                                        for (Person gparent : gparents) {
                                            if (gparent.dateOfDeath == null)
                                                System.out.println(gparent.name + " will receive " + amount);
                                        }
                                        return;
                                    }

                                    // if all grand parents are dead

                                    //if uncles exists

                                    if (uncle_aunt.size() > 0) {
                                        int aliveUncleCounter = 0;
                                        int deadUncleCounter = 0;

                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                            else deadUncleCounter++;
                                        }
                                        //if all uncles are alive
                                        if (aliveUncleCounter == uncle_aunt.size()) {
                                            for (Person uncle : uncle_aunt) {
                                                System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                            }
                                            return;
                                        }
                                        //if all uncles are dead

                                        if (deadUncleCounter == uncle_aunt.size()) {
                                            //some have children some dont
                                            int uncleHasChildren = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) uncleHasChildren++;
                                            }

                                            if (uncleHasChildren > 0) {
                                                amount = amount / uncleHasChildren;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.children.size() > 0) {
                                                        for (Person n : uncle.children) {
                                                            System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                            //if all are dead and none have children - go to govt
                                            System.out.println("Govt/Crown will receive " + amount);
                                            return;
                                        }

                                        // if some uncles are alive some are dead
                                        else {
                                            int deadUncleHasChildCounter = 0;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                    deadUncleHasChildCounter++;
                                            }
//                                //no dead uncles has children
                                            if (deadUncleHasChildCounter == 0) {
                                                amount = amount / aliveUncleCounter;
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                }
                                                return;
                                            }
//                                //some dead uncles have children
                                            else {
                                                amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                                for (Person uncle : uncle_aunt) {
                                                    if (uncle.dateOfDeath == null)
                                                        System.out.println(uncle.name + " will receive: " + amount);
                                                    else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                        for (Person gchild : uncle.children) {
                                                            System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                        }
                                                    }
                                                }
                                                return;
                                            }

                                        }
                                    }
                                    //if uncles dont exist
                                    else {
                                        System.out.println("The Govt/Crown will receive: " + amount);
                                    }
                                }
                                //if some siblings are alive some are dead
                                else {
                                    int deadSiblingHasChildCounter = 0;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                            deadSiblingHasChildCounter++;
                                    }
//                                //no dead sibling has children
                                    if (deadSiblingHasChildCounter == 0) {
                                        amount = amount / aliveSiblingsCounter;
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead sibling have children
                                    else {
                                        amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                        for (Person sibling : siblings) {
                                            if (sibling.dateOfDeath == null)
                                                System.out.println(sibling.name + " will receive: " + amount);
                                            else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                                for (Person gchild : sibling.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }

                                }
                            }
                            //if siblings dont exist
                            else {
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }

                        }
                        //some children are alive some are dead
                        else {
                            int deadHasChildCounter = 0;
                            for (Person child : children) {
                                if (child.dateOfDeath != null && child.children.size() > 0)
                                    deadHasChildCounter++;
                            }
//                                //no dead has children
                            if (deadHasChildCounter == 0) {
                                amount = amount / aliveCounter;
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                }
                                return;
                            }
//                                //some dead have children
                            else {
                                amount = amount / (aliveCounter + deadHasChildCounter);
                                for (Person child : children) {
                                    if (child.dateOfDeath == null)
                                        System.out.println(child.name + " will receive: " + amount);
                                    else if (child.dateOfDeath != null && child.children.size() > 0) {
                                        for (Person gchild : child.children) {
                                            System.out.println(gchild.name + " will receive: " + (amount / (child.children.size())));
                                        }
                                    }
                                }
                                return;
                            }

                        }
                    }
                    //if children dont exist
                    else {
                        int aliveParent = 0;
                        for (Person parent : parents) {
                            if (parent.dateOfDeath == null) aliveParent++;
                        }
                        if (aliveParent > 0) {
                            amount /= aliveParent;
                            for (Person parent : parents) {
                                if (parent.dateOfDeath == null)
                                    System.out.println(parent.name + " will receive " + amount);
                            }
                            return;
                        }
                        //none of my parents are alive

                        //if siblings exists
                        if (siblings.size() > 0) {
                            int aliveSiblingsCounter = 0;
                            int deadSiblingsCounter = 0;

                            for (Person sibling : siblings) {
                                if (sibling.dateOfDeath == null) aliveSiblingsCounter++;
                                else deadSiblingsCounter++;
                            }
                            //if all siblings are alive
                            if (aliveSiblingsCounter == siblings.size()) {
                                for (Person sibling : siblings) {
                                    System.out.println(sibling.name + "will receive " + amount / aliveSiblingsCounter);
                                }
                                return;
                            }
                            //if all siblings are dead
                            else if (deadSiblingsCounter == siblings.size()) {
                                //some dead have children some dont
                                int siblingHasChildren = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.children.size() > 0) siblingHasChildren++;
                                }
                                if (siblingHasChildren > 0) {
                                    amount = amount / siblingHasChildren;
                                    for (Person sibling : siblings) {
                                        if (sibling.children.size() > 0) {
                                            for (Person n : sibling.children) {
                                                System.out.println(n.name + " will receive " + amount / sibling.children.size());
                                            }
                                        }
                                    }
                                    return;
                                }

                                //if all are dead and none have children - go to grandparents
                                int aliveGrandParent = 0;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null) aliveGrandParent++;
                                }
                                if (aliveGrandParent > 0) {
                                    amount /= aliveGrandParent;
                                    for (Person gparent : gparents) {
                                        if (gparent.dateOfDeath == null)
                                            System.out.println(gparent.name + " will receive " + amount);
                                    }
                                    return;
                                }

                                // if all grand parents are dead

                                //if uncles exists

                                if (uncle_aunt.size() > 0) {
                                    int aliveUncleCounter = 0;
                                    int deadUncleCounter = 0;

                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                        else deadUncleCounter++;
                                    }
                                    //if all uncles are alive
                                    if (aliveUncleCounter == uncle_aunt.size()) {
                                        for (Person uncle : uncle_aunt) {
                                            System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                        }
                                        return;
                                    }
                                    //if all uncles are dead

                                    if (deadUncleCounter == uncle_aunt.size()) {
                                        //some have children some dont
                                        int uncleHasChildren = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) uncleHasChildren++;
                                        }

                                        if (uncleHasChildren > 0) {
                                            amount = amount / uncleHasChildren;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.children.size() > 0) {
                                                    for (Person n : uncle.children) {
                                                        System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        //if all are dead and none have children - go to govt
                                        System.out.println("Govt/Crown will receive " + amount);
                                        return;
                                    }

                                    // if some uncles are alive some are dead
                                    else {
                                        int deadUncleHasChildCounter = 0;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                                deadUncleHasChildCounter++;
                                        }
//                                //no dead uncles has children
                                        if (deadUncleHasChildCounter == 0) {
                                            amount = amount / aliveUncleCounter;
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                            }
                                            return;
                                        }
//                                //some dead uncles have children
                                        else {
                                            amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                            for (Person uncle : uncle_aunt) {
                                                if (uncle.dateOfDeath == null)
                                                    System.out.println(uncle.name + " will receive: " + amount);
                                                else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                    for (Person gchild : uncle.children) {
                                                        System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                    }
                                                }
                                            }
                                            return;
                                        }

                                    }
                                }
                                //if uncles dont exist
                                else {
                                    System.out.println("The Govt/Crown will receive: " + amount);
                                }
                            }
                            //if some siblings are alive some are dead
                            else {
                                int deadSiblingHasChildCounter = 0;
                                for (Person sibling : siblings) {
                                    if (sibling.dateOfDeath != null && sibling.children.size() > 0)
                                        deadSiblingHasChildCounter++;
                                }
//                                //no dead sibling has children
                                if (deadSiblingHasChildCounter == 0) {
                                    amount = amount / aliveSiblingsCounter;
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                    }
                                    return;
                                }
//                                //some dead sibling have children
                                else {
                                    amount = amount / (aliveSiblingsCounter + deadSiblingHasChildCounter);
                                    for (Person sibling : siblings) {
                                        if (sibling.dateOfDeath == null)
                                            System.out.println(sibling.name + " will receive: " + amount);
                                        else if (sibling.dateOfDeath != null && sibling.children.size() > 0) {
                                            for (Person gchild : sibling.children) {
                                                System.out.println(gchild.name + " will receive: " + (amount / (sibling.children.size())));
                                            }
                                        }
                                    }
                                    return;
                                }

                            }
                        }
                        //if siblings dont exist
                        else {
                            int aliveGrandParent = 0;
                            for (Person gparent : gparents) {
                                if (gparent.dateOfDeath == null) aliveGrandParent++;
                            }
                            if (aliveGrandParent > 0) {
                                amount /= aliveGrandParent;
                                for (Person gparent : gparents) {
                                    if (gparent.dateOfDeath == null)
                                        System.out.println(gparent.name + " will receive " + amount);
                                }
                                return;
                            }

                            // if all grand parents are dead

                            //if uncles exists

                            if (uncle_aunt.size() > 0) {
                                int aliveUncleCounter = 0;
                                int deadUncleCounter = 0;

                                for (Person uncle : uncle_aunt) {
                                    if (uncle.dateOfDeath == null) aliveUncleCounter++;
                                    else deadUncleCounter++;
                                }
                                //if all uncles are alive
                                if (aliveUncleCounter == uncle_aunt.size()) {
                                    for (Person uncle : uncle_aunt) {
                                        System.out.println(uncle.name + "will receive " + amount / aliveUncleCounter);
                                    }
                                    return;
                                }
                                //if all uncles are dead

                                if (deadUncleCounter == uncle_aunt.size()) {
                                    //some have children some dont
                                    int uncleHasChildren = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.children.size() > 0) uncleHasChildren++;
                                    }

                                    if (uncleHasChildren > 0) {
                                        amount = amount / uncleHasChildren;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.children.size() > 0) {
                                                for (Person n : uncle.children) {
                                                    System.out.println(n.name + " will receive " + amount / uncle.children.size());
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    //if all are dead and none have children - go to govt
                                    System.out.println("Govt/Crown will receive " + amount);
                                    return;
                                }

                                // if some uncles are alive some are dead
                                else {
                                    int deadUncleHasChildCounter = 0;
                                    for (Person uncle : uncle_aunt) {
                                        if (uncle.dateOfDeath != null && uncle.children.size() > 0)
                                            deadUncleHasChildCounter++;
                                    }
//                                //no dead uncles has children
                                    if (deadUncleHasChildCounter == 0) {
                                        amount = amount / aliveUncleCounter;
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                        }
                                        return;
                                    }
//                                //some dead uncles have children
                                    else {
                                        amount = amount / (aliveUncleCounter + deadUncleHasChildCounter);
                                        for (Person uncle : uncle_aunt) {
                                            if (uncle.dateOfDeath == null)
                                                System.out.println(uncle.name + " will receive: " + amount);
                                            else if (uncle.dateOfDeath != null && uncle.children.size() > 0) {
                                                for (Person gchild : uncle.children) {
                                                    System.out.println(gchild.name + " will receive: " + (amount / (uncle.children.size())));
                                                }
                                            }
                                        }
                                        return;
                                    }
                                }
                            }
                            //if uncles dont exist
                            else {
                                System.out.println("The Govt/Crown will receive: " + amount);
                            }
                        }
                    }
                }
                break;
            default:
                System.out.println("Invalid input");
                break;
        }


    }

    public static void main(String[] args) {

        // Input for Deceased person
        System.out.println("Enter deceased person's name:");
        String name = scanner.nextLine();

        System.out.println("Enter date of birth (yyyy-MM-dd):");
        Date dob = parseDate(scanner.nextLine());

        System.out.println("Enter date of death (yyyy-MM-dd):");
        Date dod = parseDate(scanner.nextLine());

        while (dob != null && dod != null && dod.before(dob)) {
            System.out.println("Date of death cannot be before date of birth. Please re-enter.");
            System.out.println("Enter date of death (yyyy-MM-dd):");
            dod = parseDate(scanner.nextLine());
        }

        System.out.println("Enter gender:");
        String gender = scanner.nextLine();

        System.out.println("Enter place of birth:");
        String pob = scanner.nextLine();

        Deceased deceased = new Deceased(name, dob, dod, gender, pob, "self");

        System.out.println("Enter number of marriages:");
        int numMarriages = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < numMarriages; i++) {
            System.out.println("Enter marriage date (yyyy-MM-dd):");
            Date marriageDate = parseDate(scanner.nextLine());

            System.out.println("Enter divorce date (yyyy-MM-dd):");
            Date divorceDate = parseDate(scanner.nextLine());

            while (marriageDate != null && divorceDate != null && divorceDate.before(marriageDate)) {
                System.out.println("Divorce date cannot be before marriage date. Please re-enter.");
                System.out.println("Enter divorce date (yyyy-MM-dd):");
                divorceDate = parseDate(scanner.nextLine());
            }

            deceased.addMarriage(new Marriage(marriageDate, divorceDate));
        }

        if (numMarriages == 0) {
            System.out.println("The deceased was not married.");
        }

        deceased.displayDetails();
        System.out.println("\nFamily Member Details:");
        String[] familyMembers = {"parent", "parent", "grandparent", "grandparent", "grandparent", "grandparent"};


        for (String person : familyMembers) {
            deceased.addFamilyMember(getDetails(person));
        }
        if (numMarriages > 0) {
            deceased.addFamilyMember(getDetails("spouse"));
        }
        //uncle/aunt and cousins
        System.out.println("Enter the number of Uncles/aunt");
        int numUncles = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < numUncles; i++) {
            Person uncle_aunt = getDetails("uncle/aunt");
            System.out.println("Enter the number of Children of uncle/aunt" + (i + 1));
            int numChild = Integer.parseInt(scanner.nextLine());
            for (int j = 0; j < numChild; j++) {
                uncle_aunt.children.add(getDetails("cousin"));
            }
            deceased.addFamilyMember(uncle_aunt);
        }
        //siblings
        System.out.println("Enter the number of Siblings");
        int siblings = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < siblings; i++) {
            Person sibling = getDetails("sibling");
            System.out.println("Enter the number of Children of sibling " + (i + 1));
            int numChild = Integer.parseInt(scanner.nextLine());
            for (int j = 0; j < numChild; j++) {
                sibling.children.add(getDetails("nephew/niece"));
            }
            deceased.addFamilyMember(sibling);
        }
        //children and grandchildren
        System.out.println("Enter the number of Children");
        int childs = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < childs; i++) {
            Person child = getDetails("child");
            System.out.println("Enter the number of Children of child " + (i + 1));
            int numChild = Integer.parseInt(scanner.nextLine());
            for (int j = 0; j < numChild; j++) {
                child.children.add(getDetails("grandchild"));
            }
            deceased.addFamilyMember(child);
        }

        deceased.displayFamilyDetails();

        will(deceased);

        scanner.close();
    }
}