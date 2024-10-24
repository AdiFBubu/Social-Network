package ubb.scs.map;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.Tuple;
import ubb.scs.map.domain.User;
import ubb.scs.map.domain.validators.FriendshipValidator;
import ubb.scs.map.domain.validators.UserValidator;
import ubb.scs.map.repository.file.FriendshipRepository;
import ubb.scs.map.repository.file.UserRepository;
import ubb.scs.map.service.SocialNetwork;

import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

/*
    graf neorientat
    o pereche: tuplu id utiliz 1, id utiliz 2
 */

public class Main {

    public static void menu() {
        System.out.println("--------------MENU-------------\n");
        System.out.println("1. Add User");
        System.out.println("2. Delete User");
        System.out.println("3. Add Friend");
        System.out.println("4. Delete Friend");
        System.out.println("5. Show the number of communities");
        System.out.println("6. Show the most sociable community");
        System.out.println("7. Exit");
    }

    public static void main(String[] args) {

        UserRepository userRepo = new UserRepository(new UserValidator(), "./data/users.txt");
        FriendshipRepository friendshipRepo = new FriendshipRepository(userRepo, new FriendshipValidator(), "./data/friendships.txt");
        long id;
        if (userRepo.getEntities().isEmpty())
            id = 1L;
        else {
            id = -1L;
            for (User u : userRepo.findAll())
                id = Math.max(id, u.getId());
            id += 1;
        }
        while (true) {
            menu();
            Scanner scanner = new Scanner(System.in);
            int cmd;
            try {
                cmd = scanner.nextInt();
            }
            catch (Exception e) {
                System.out.println("Enter an integer!");
                continue;
            }
            if (!(cmd >= 1 && cmd <= 7)) {
                System.out.println("Invalid command!");
                continue;
            }
            if (cmd == 1) {
                scanner.nextLine();
                System.out.println("Enter the user first name: ");
                String firstName = scanner.next();
                System.out.println("Enter the user last name: ");
                String lastName = scanner.next();
                User user = new User(firstName, lastName);
                user.setId(id);
                id ++;
                try {
                    var u = userRepo.save(user);
                    if (u != null)
                        System.out.println("User is already added!");
                    else
                        System.out.println("Successfully added user!");
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (cmd == 2) {
                System.out.println("Enter user id: ");
                Long userID;
                try{
                    userID = scanner.nextLong();
                }
                catch (Exception e) {
                    System.out.println("Enter an integer!");
                    continue;
                }
                User user = userRepo.findOne(userID);
                try{
                    var u = userRepo.delete(userID);
                    if (u == null)
                        System.out.println("User not found!");
                    else {
                        friendshipRepo.verifyUsers();
                        System.out.println("Successfully deleted user!");
                    }
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (cmd == 3) {
                System.out.println("Enter the first user id: ");
                long user1ID;
                try{
                    user1ID = scanner.nextLong();
                }
                catch (Exception e) {
                    System.out.println("Enter an integer!");
                    continue;
                }
                long user2ID;
                System.out.println("Enter the second user id: ");
                try{
                    user2ID = scanner.nextLong();
                }
                catch (Exception e) {
                    System.out.println("Enter an integer!");
                    continue;
                }
                Friendship f = new Friendship();
                f.setId(new Tuple<>(user1ID, user2ID));
                try {
                    var u = friendshipRepo.save(f);
                    if (u != null)
                        System.out.println("Friendship is already added!");
                    else
                        System.out.println("Successfully added friendship!");
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (cmd == 4){
                System.out.println("Enter the first user id: ");
                long user1ID;
                try{
                    user1ID = scanner.nextLong();
                }
                catch (Exception e) {
                    System.out.println("Enter an integer!");
                    continue;
                }
                long user2ID;
                System.out.println("Enter the second user id: ");
                try{
                    user2ID = scanner.nextLong();
                }
                catch (Exception e) {
                    System.out.println("Enter an integer!");
                    continue;
                }
                try {
                    var u = friendshipRepo.delete(new Tuple<>(user1ID, user2ID));
                    if (u == null)
                        System.out.println("Friendship not found!");
                    else
                        System.out.println("Successfully deleted friendship!");
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (cmd == 5){
                SocialNetwork f = new SocialNetwork(userRepo, friendshipRepo);
                var rez = f.getCommunities();
                System.out.println("There are " + rez.size() + " communities");
                int nr = 1;
                for (var c : rez) {
                    System.out.println(nr);
                    nr ++;
                    for (var el : c)
                        System.out.println(el);
                    System.out.println('\n');
                }
            }
            else if (cmd == 6) {
                SocialNetwork f = new SocialNetwork(userRepo, friendshipRepo);
                var rez = f.MostSociableCommunity();
                System.out.println("The most sociable community is: ");
                for (var el : rez) {
                    System.out.println(el);
                }
            }

            else
                break;
        }
        System.out.println("Byeeee");






        /*
        Repository<Long, Utilizator> repo = new InMemoryRepository<Long, Utilizator>(new UtilizatorValidator());
        UserRepository repoFile = new UserRepository(new UserValidator(), "./data/users.txt");
        User u1 = new User("Adi", "G");
        User u2 = new User("Marius", "R");
        User u3 = new User("Ioana", "O");
        User u4 = new User("Marin", "I");
        User u5 = new User("Ion", "E");
        User u6 = new User("Cleopatra", "Z");
        User u7 = new User("Rufus", "P");
        User u8 = new User("Eleonora", "O");
        u1.setId(1L); u2.setId(2L); u3.setId(3L); u4.setId(4L);
        u5.setId(5L); u6.setId(6L); u7.setId(7L); u8.setId(8L);
        repoFile.save(u1); repoFile.save(u2); repoFile.save(u3); repoFile.save(u4);
        repoFile.save(u5); repoFile.save(u6); repoFile.save(u7); repoFile.save(u8);
        Friendship f1 = new Friendship();
        f1.setId(new Tuple<>(u1.getId(), u2.getId()));
        Friendship f2 = new Friendship();
        f2.setId(new Tuple<>(u1.getId(), u3.getId()));
        Friendship f3 = new Friendship();
        f3.setId(new Tuple<>(u5.getId(), u6.getId()));
        FriendshipRepository friendsRepo = new FriendshipRepository(new FriendshipValidator(), "./data/friendships.txt");
        friendsRepo.save(f1); friendsRepo.save(f2); friendsRepo.save(f3);
        FriendshipAnalyzer f = new FriendshipAnalyzer(repoFile.getEntities(), friendsRepo.getEntities());
        var rez = f.getCommunities();
        System.out.println("sunt " + rez.size() + " comunitati\n");
        for (var c : rez) {
            for (var el : c)
                System.out.println(el + " ,");
            System.out.println('\n');
        }
        var rez2 = f.MostSociableCommunity();
        System.out.println("most sociable community " + rez2.size() + " comunitati\n");
        for (var el : rez2) {
            System.out.println(el + " ,");
        }
        try {
            repoFile.save(u1);
            repoFile.save(u2);
            //repoFile.save(u3);
        }catch(IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }catch(ValidationException e)
        {
            System.out.println(e.getMessage());
        }
        try{

            Utilizator u;
            u = repoFile.update(u1);
            if (u == null)
                System.out.println("Utilizatorul a fost updatat");
            else
                System.out.println("Utilizatorul nu a fost updatat");
            u = repoFile.update(u2);
            if (u == null)
                System.out.println("Utilizatorul a fost updatat");
            else
                System.out.println("Utilizatorul nu a fost updatat");
            u = repoFile.update(u3);
            if (u == null)
                System.out.println("Utilizatorul a fost updatat");
            else
                System.out.println("Utilizatorul nu a fost updatat");


            Utilizator u;
            u = repoFile.delete(u1.getId());
            if (u != null)
                System.out.println("Utilizatorul a fost sters");
            else
                System.out.println("Utilizatorul nu a fost sters");
            u = repoFile.delete(u2.getId());
            if (u != null)
                System.out.println("Utilizatorul a fost sters");
            else
                System.out.println("Utilizatorul nu a fost sters");
            u = repoFile.delete(u3.getId());
            if (u != null)
                System.out.println("Utilizatorul a fost sters");
            else
                System.out.println("Utilizatorul nu a fost sters");
        }
        catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        catch(ValidationException e) {
            System.out.println(e.getMessage());
        }
        */

    }
}