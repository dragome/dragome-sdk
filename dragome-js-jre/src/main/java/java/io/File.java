/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package java.io;

import java.net.URI;

public class File implements Serializable, Comparable<File> {

    public static final char separatorChar = '/';

    public static final String separator = "" + separatorChar;

    public static final char pathSeparatorChar = ':';

    public static final String pathSeparator = "" + pathSeparatorChar;

    public static final File ROOT = new File("");

    File parent;
    String name;

    public File(String pathname) {
        while (pathname.endsWith(separator) && pathname.length() > 0) {
            pathname = pathname.substring(0, pathname.length() - 1);
        }

        int cut = pathname.lastIndexOf(separatorChar);
        if (cut == -1) {
            name = pathname;
        } else if (cut == 0) {
            name = pathname.substring(cut);
            parent = name.length() == 0 ? null : ROOT;
        } else {
            name = pathname.substring(cut + 1);
            parent = new File(pathname.substring(0, cut));
        }
    }

    public File(String parent, String child) {
        this(new File(parent), child);
    }

    public File(File parent, String child) {
        this.parent = parent;
        this.name = child;
    }

    public File(URI uri) {
        // TODO Auto-generated constructor stub
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent == null ? "" : parent.getPath();
    }

    public File getParentFile() {
        return parent;
    }

    public String getPath() {
        return parent == null ? name
                : (parent.getPath() + separatorChar + name);
    }

    private boolean isRoot() {
        return name.length() == 0 && parent == null;
    }

    public boolean isAbsolute() {
        if (isRoot()) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        return parent.isAbsolute();
    }

    public String getAbsolutePath() {
        String path = getAbsoluteFile().getPath();
        return path.length() == 0 ? "/" : path;
    }

    public File getAbsoluteFile() {
        if (isAbsolute()) {
            return this;
        }
        if (parent == null) {
            return new File(ROOT, name);
        }
        return new File(parent.getAbsoluteFile(), name);
    }

    public String getCanonicalPath() {
        return getCanonicalFile().getAbsolutePath();
    }

    public File getCanonicalFile() {
        File cParent = parent == null ? null : parent.getCanonicalFile();
        if (name.equals(".")) {
            return cParent == null ? ROOT : cParent;
        }
        if (cParent != null && cParent.name.length() == 0) {
            cParent = null;
        }
        if (name.equals("..")) {
            if (cParent == null) {
                return ROOT;
            }
            if (cParent.parent == null) {
                return ROOT;
            }
            return cParent.parent;
        }
        if (cParent == null && name.length() > 0) {
            return new File(ROOT, name);
        }
        return new File(cParent, name);
    }

    public boolean canRead() {
        return true;
    }

    public boolean canWrite() {
        return true;
    }

    public boolean exists() {
        return true;
    }

    public boolean isDirectory() {
        return false;
    }

    public boolean isFile() {

        return false;

    }

    public boolean isHidden() {
        return false;
    }

    public long lastModified() {
        return 0;
    }

    public long length() {

        return 0;

    }

    public boolean createNewFile() throws IOException {
        if (exists()) {
            return false;
        }

        return parent.exists();
    }

    public boolean delete() {

        return exists();

    }

    public void deleteOnExit() {
        throw new RuntimeException("not implemented");
    }

    public String[] list() {
        throw new RuntimeException("not implemented");
    }

    public File[] listFiles() {
        return listFiles(null);
    }

    public File[] listFiles(FilenameFilter filter) {

        return null;
    }

    public boolean mkdir() {

        if (parent != null && !parent.exists()) {
            return false;
        }

        return !exists();

    }

    public boolean mkdirs() {
        if (parent != null) {
            parent.mkdirs();
        }
        return mkdir();
    }

    public boolean renameTo(File dest) {
        throw new RuntimeException("renameTo()");
    }

    public boolean setLastModified(long time) {
        return false;
    }

    public boolean setReadOnly() {
        return false;
    }

    public static File[] listRoots() {
        return new File[]{ROOT};
    }

    public static File createTempFile(String prefix, String suffix,
            File directory) throws IOException {
        throw new RuntimeException("not implemented");
    }

    public static File createTempFile(String prefix, String suffix)
            throws IOException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof File)) {
            return false;
        }
        return getPath().equals(((File) obj).getPath());
    }

    @Override
    public int hashCode() {
        return parent != null ? parent.hashCode() + name.hashCode() : name
                .hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public int compareTo(File another) {
        return 0;
    }

    public Object toURI() {
        // TODO Auto-generated method stub
        return null;
    }
}
