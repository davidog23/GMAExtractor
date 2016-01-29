package net.davidog.gmad;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


//https://github.com/garrynewman/gmad
public class GMAD {

    //Debería usarla?
    private static List<String> allowedFoldersToChange = Arrays.asList("gamemodes", "maps", "lua", "materials", "models", "sound");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException {
	    Runtime runtime = Runtime.getRuntime();
        Path garrysGMAD = Paths.get("C:\\Program Files (x86)\\Steam\\steamapps\\common\\GarrysMod\\bin\\gmad.exe");

        if(args[0] != null) {
            File arg = new File(args[0]);

            if (arg.isDirectory()) {
                File garrysFolder = arg.getParentFile();

                //Descompresión de los .gma
                List<File> files = Arrays.asList(arg.listFiles(File::isFile));
                files.removeIf(t -> !t.getName().endsWith(".gma"));
                files.forEach(t -> {
                    try {
                        runtime.exec(garrysGMAD.toString() + " " + t.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                //Mover carpetas
                List<File> folders = Arrays.asList(arg.listFiles(File::isDirectory)); //folders = ds_XXXXXX
                for(File f : folders) {
                    move(f, garrysFolder);
                }

            }
        } else {
            System.out.println("Especifica la carpeta donde estan los .gma");
        }
    }

    public static void move(File addonsFolder, File garrysFolder) throws IOException {

        Path addonsFolderPath = addonsFolder.toPath();
        Path garrysFolderPath = garrysFolder.toPath();

        Files.walkFileTree(addonsFolderPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path target = garrysFolderPath.resolve(addonsFolderPath.relativize(dir));
                if(Files.notExists(target)) {
                    Files.copy(dir, target);
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path target = garrysFolderPath.resolve(addonsFolderPath.relativize(file));
                if(Files.notExists(target)) {
                    Files.copy(file, target);
                }
                return FileVisitResult.CONTINUE;
            }

//            @Override
//            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                Files.delete(dir);
//                return FileVisitResult.CONTINUE;
//            }
        });
    }
}
