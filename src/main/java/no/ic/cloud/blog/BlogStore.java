package no.ic.cloud.blog;

import java.util.List;

public interface BlogStore {

    List<String> getThreads();

    void addThread(String name);

    List<String> getPosts(String thread);

    void addPost(String thread, String post);

}
