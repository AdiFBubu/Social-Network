package ubb.scs.map;

import jdk.jshell.execution.Util;
import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.Tuple;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.domain.validators.UtilizatorValidator;
import ubb.scs.map.domain.validators.ValidationException;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.repository.file.UtilizatorRepository;
import ubb.scs.map.repository.memory.InMemoryRepository;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

/*
    graf neorrientat
    o pereche: tuplu id utiliz 1, id utiliz 2
 */

public class Main {
    public static void main(String[] args) {

        Repository<Long, Utilizator> repo = new InMemoryRepository<Long, Utilizator>(new UtilizatorValidator());
        Repository<Long, Utilizator> repoFile = new UtilizatorRepository(new UtilizatorValidator(), "./data/utilizatori.txt");
        Utilizator u1 = new Utilizator("AlexiaUpdatata", "c");
        Utilizator u2 = new Utilizator("MariusUpdatat", "h");
        Utilizator u3 = new Utilizator("JohnUpdatat", "e");
        u1.setId(3L);
        u2.setId(5L);
        u3.setId(11L);
        Friendship f = new Friendship();
        f.setId(new Tuple<Utilizator, Utilizator>(u1, u2));
        Friendship f2 = new Friendship();
        f2.setId(new Tuple<Utilizator, Utilizator>(u2, u1));
        System.out.println(new Tuple<Utilizator, Utilizator>(u1, u2).equals(new Tuple<Utilizator, Utilizator>(u2, u1)));
        System.out.println(f.equals(f2));
        /*
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
        System.out.println();

    }
}