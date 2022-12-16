package com.dci.intellij.dbn.common.util;

import com.dci.intellij.dbn.language.common.DBLanguageFileType;
import com.dci.intellij.dbn.language.common.DBLanguagePsiFile;
import com.dci.intellij.dbn.vfs.DatabaseFileSystem;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public final class Files {
    private Files() {}

    public static File createFileByRelativePath(@NotNull final File absoluteBase, @NotNull final String relativeTail) {
        // assert absoluteBase.isAbsolute() && absoluteBase.isDirectory(); : assertion seem to be too costly

        File point = absoluteBase;
        final String[] parts = relativeTail.replace('\\', '/').split("/");
        // do not validate, just apply rules
        for (String part : parts) {
            final String trimmed = part.trim();
            if (trimmed.length() == 0) continue;
            if (Objects.equals(trimmed, ".")) continue;
            if (Objects.equals(trimmed, "..")) {
                point = point.getParentFile();
                if (point == null) return null;
                continue;
            }
            point = new File(point, trimmed);
        }
        return point;
    }

    public static String convertToRelativePath(Project project, String path) {
        if (!Strings.isEmptyOrSpaces(path)) {
            VirtualFile baseDir = project.getBaseDir();
            if (baseDir != null) {
                File projectDir = new File(baseDir.getPath());
                String relativePath = com.intellij.openapi.util.io.FileUtil.getRelativePath(projectDir, new File(path));
                if (relativePath != null) {
                    if (relativePath.lastIndexOf(".." + File.separatorChar) < 1) {
                        return relativePath;
                    }
                }
            }
        }
        return path;
    }

    public static String convertToAbsolutePath(Project project, String path) {
        if (!Strings.isEmptyOrSpaces(path)) {
            VirtualFile baseDir = project.getBaseDir();
            if (baseDir != null) {
                File projectDir = new File(baseDir.getPath());
                if (new File(path).isAbsolute()) {
                    return path;
                } else {
                    File file = Files.createFileByRelativePath(projectDir, path);
                    return file == null ? null : file.getPath();
                }
            }
        }
        return path;
    }

    public static boolean isValidFileUrl(String fileUrl, Project project) {
        DatabaseFileSystem databaseFileSystem = DatabaseFileSystem.getInstance();
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

        if (databaseFileSystem.isDatabaseUrl(fileUrl)) {
            if (!databaseFileSystem.isValidPath(fileUrl, project)) {
                return false;
            }
        } else if (fileUrl.startsWith("file://")) {
            VirtualFile virtualFile = localFileSystem.findFileByPath(fileUrl.substring(7));
            if (virtualFile == null) {
                return false;
            }
        }
        return true;
    }

    public static File findFileRecursively(File directory, String fileName) {
        File[] files = Commons.nvl(directory.listFiles(), new File[0]);
        return Arrays.stream(files).
                filter(f -> !f.isDirectory() && Objects.equals(f.getName(), fileName)).
                findFirst().
                orElseGet(() -> Arrays.stream(files).
                        filter(f -> f.isDirectory()).
                        map(f -> findFileRecursively(f, fileName)).
                        filter(f -> f != null).findFirst().orElse(null));
    }

    public static File getPluginDeploymentRoot() {
        IdeaPluginDescriptor pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId("DBN"));
        return Objects.requireNonNull(pluginDescriptor).getPath();
    }

    public static boolean isDbLanguageFile(VirtualFile file) {
        return file.getFileType() instanceof DBLanguageFileType;
    }

    public static boolean isDbLanguagePsiFile(PsiFile psiFile) {
        return psiFile instanceof DBLanguagePsiFile;
    }
}