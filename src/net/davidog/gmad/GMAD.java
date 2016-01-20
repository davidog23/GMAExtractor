package net.davidog.gmad;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


//https://github.com/garrynewman/gmad
public class GMAD {

    private static File garrysFolder;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
	    Runtime runtime = Runtime.getRuntime();

        if(args[0] != null) {
            File arg = new File(args[0]);

            if (arg.isDirectory()) {
                garrysFolder = arg.getParentFile();

                //Descompresión de los .gma
                List<File> files = Arrays.asList(arg.listFiles(File::isFile));
                files.removeIf(t -> (new File(t.getName().substring(0, t.getName().indexOf(".")))).exists() || !t.getName().endsWith(".gma"));
                files.forEach(t -> {
                    try {
                        runtime.exec("C:\\Program Files (x86)\\Steam\\steamapps\\common\\GarrysMod\\bin\\gmad.exe " + t.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                //Mover carpetas
                List<File> folders = Arrays.asList(arg.listFiles(File::isDirectory)); //folders = ds_XXXXXX
                ArrayList<File> archivosAMover = new ArrayList<>(); //archivosAMover = Cosas dentro de maps y gamemode
                folders.forEach(t -> Arrays.asList(t.listFiles(File::isDirectory)).forEach(r -> archivosAMover.addAll(Arrays.asList(r.listFiles(GMAD::isNotInGarrysFolder)))));

                String s = File.separator;
                archivosAMover.forEach(f -> {
                    try {
                        File target = new File(garrysFolder.getAbsolutePath() + s + f.getParentFile().getName() + s + f.getName());
                        if(!target.getParentFile().exists()) { target.getParentFile().mkdir(); }
                        if(f.isFile()) {
                            Files.copy(f.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            copyDir(f, target);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } else {
            System.out.println("Especifica la carpeta donde estan los .gma");
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isNotInGarrysFolder(File file) {
        String name = file.getName();
        ArrayList<File> yaDescomprimido = new ArrayList<>();
        Arrays.asList(garrysFolder.listFiles(f -> f.getName().equals("gamemodes") || f.getName().equals("maps"))).forEach(f -> yaDescomprimido.addAll(Arrays.asList(f.listFiles())));

        boolean control = true;
        int n = yaDescomprimido.size();
        for (int i = 0; i < n && control; i++) {
            if(name.equals(yaDescomprimido.get(i).getName())){
                control = false;
            }
        }
        return control;
    }

    //Sacado de los javadocs de la api...
    public static void copyDir(File sourceFolder, File targetFolder) throws IOException {
        Path source = sourceFolder.toPath();
        Path target = targetFolder.toPath();

        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                            throws IOException
                    {
                        Path targetdir = target.resolve(source.relativize(dir));
                        try {
                            Files.copy(dir, targetdir);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetdir))
                                throw e;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException
                    {
                        Files.copy(file, target.resolve(source.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                });
    }
}
