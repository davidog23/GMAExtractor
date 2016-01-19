package net.davidog.gmad;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class GMAD {

    public static void main(String[] args) {
	    Runtime runtime = Runtime.getRuntime();
        String userprofile = System.getenv("USERPROFILE");

        if(args[0] != null) {
            File arg = new File(args[0]);

            if (arg.isDirectory()) {
                File garrysFolder = arg.getParentFile();

                //Descompresión de los .gma
                List<File> files = Arrays.asList(arg.listFiles(f -> f.isFile()));
                files.removeIf(t -> (new File(t.getName().substring(0, t.getName().indexOf(".")))).exists() || !t.getName().endsWith(".gma"));
                files.forEach(t -> {
                    try {
                        runtime.exec("C:\\Program Files (x86)\\Steam\\steamapps\\common\\GarrysMod\\bin\\gmad.exe " + t.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                //Mover carpetas
                List<File> folders = Arrays.asList(arg.listFiles(f -> f.isDirectory())); //folders = ds_XXXXXX
                Vector<File> archivosAMover = new Vector<>(); //archivosAMover = Cosas dentro de maps y gamemode
                folders.forEach(t -> Arrays.asList(t.listFiles(f -> f.isDirectory())).forEach(r -> {
                    if(r.getName().equals("gamemode") || r.getName().equals("maps")) {
                        archivosAMover.addAll(Arrays.asList(r.listFiles(f -> true))); //Continuar por aqui.
                    }
                }));

            }
        }
    }
}
