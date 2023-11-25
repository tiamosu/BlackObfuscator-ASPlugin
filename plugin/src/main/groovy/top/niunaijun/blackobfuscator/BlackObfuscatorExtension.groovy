package top.niunaijun.blackobfuscator

import org.gradle.api.Project

class BlackObfuscatorExtension {
    int type = 0
    boolean enabled = false
    int depth = 1
    String[] obfClass = []
    String[] blackClass = []

    BlackObfuscatorExtension(Project project) {
    }

    @Override
    String toString() {
        return "BlackObfuscatorExtension{" +
                "type=" + type +
                ", enabled=" + enabled +
                ", depth=" + depth +
                ", obfClass=" + Arrays.toString(obfClass) +
                ", blackClass=" + Arrays.toString(blackClass) +
                '}';
    }
}