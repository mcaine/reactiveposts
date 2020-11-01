package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mikeycaine.reactiveposts.model.Thread;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactiveSAClient implements Client {
    private final static int MAX_REQUEST_PAGE_COUNT = 100;
    private final static int MAX_CONCURRENT_REQUESTS = 1;

    private final WebClient webClient;

    @Override
    public Flux<Forum> retrieveForums() {
        return retrieveBodyAsMono(Urls.forumIndex())
            .flatMapMany(this::parseIndexPage);
    }

    @Override
    public Flux<Thread> retrieveThreads(Forum forum, int pageId) {
        return retrieveBodyAsMono(Urls.forumIndexAddress(forum.getId(), pageId))
            .flatMapMany(body -> parseToThreadsFlux(body, forum, pageId));

    }

    @Override
    public Flux<Thread> retrieveThreads(Forum forum, int pageId, int endPageId) {

        // TODO
        return Flux.empty();

    }

    @Override
    public Flux<Post> retrievePosts(Thread thread, int pageId) {
        log.info("Retrieving posts for " + thread.getId() + ", Page=" + pageId + ")");
        validatePageIdParam(pageId);
        return retrieveBodyAsMono(Urls.pageAddress(thread.getId(), pageId))
            .flatMapMany(body -> parseToPostsFlux(body, thread, pageId));
    }

    @Override
    public Flux<Post> retrievePosts(Thread thread, int startPageId, int endPageId) {
        log.info("Retrieving posts for " + thread + ", Pages=" + startPageId + "->" + endPageId + ")");
        int count = validatePageRangeParams(startPageId, endPageId);
        return Flux.range(startPageId, count)
            .flatMapSequential(pageId -> Flux.defer(() -> retrievePosts(thread, pageId)), MAX_CONCURRENT_REQUESTS);
    }



    @Override
    public Mono<Integer> latestPageId(Thread thread) {
        return retrieveBodyAsMono(Urls.pageAddress(thread.getId(), 1))
            .flatMap(this::parseLatestPageId);
    }

    ////////////////////////////////////////////////////////////////////

    private void validatePageIdParam(int pageId) {
        if (pageId < 1 || pageId > 100000) {
            throw new IllegalArgumentException("Page ID " + pageId + " out of allowed range");
        }
    }

    private int validatePageRangeParams(int startPageId, int endPageId) {
        validatePageIdParam(startPageId);
        validatePageIdParam(endPageId);
        int count = endPageId - startPageId + 1;
        if (count > MAX_REQUEST_PAGE_COUNT) {
            throw new IllegalArgumentException("Can't retrieve more than " + MAX_REQUEST_PAGE_COUNT + " pages at a time");
        }
        return count;
    }

    private Mono<String> retrieveBodyAsMono(String url) {
        return webClient
            .method(HttpMethod.GET)
            .uri(url)
            .retrieve()
            .bodyToMono(String.class);
    }

    private Flux<Forum> parseIndexPage(String bodyText) {
        return Flux.fromStream(forumStreamFromPage(bodyText));
    }

    private Stream<Forum> forumStreamFromPage(String bodyText) {
        //log.info("PARSING INDEX PAGE\n" + bodyText);
        Element body = Jsoup.parse(bodyText).body();
        Elements forumElements = body.getElementById("forums").getElementsByClass("forum");

        return forumElements.stream().flatMap(element ->
            element.getElementsByClass("title").stream()).flatMap(titleElement -> {

            Element forumElement = titleElement.getElementsByClass("forum").first();
            String forumName = forumElement.text();
            return forumIdFromHref(forumElement.attr("href")).stream()
                .map(fid -> new Forum(fid, forumName, subForums(titleElement)));
        });
    }

    private Set<Forum> subForums(Element titleElement) {
        return titleElement.getElementsByClass("subforums").stream().flatMap(subforumsElement ->
            subforumsElement.getElementsByTag("a").stream().flatMap(subforumLinkElement ->
                forumIdFromHref(subforumLinkElement.attr("href")).stream()
                    .map(subForumId -> new Forum(subForumId, subforumLinkElement.text()))
            )
        ).collect(Collectors.toSet());
    }

    static Optional<Integer> forumIdFromHref(String href) {
        Pattern pattern = Pattern.compile("(.*)forumid=(\\d+)");
        Matcher matcher = pattern.matcher(href);
        return matcher.find() ? Optional.of(Integer.parseInt(matcher.group(2))) : Optional.empty();
    }

    private Mono<Integer> parseLatestPageId(String bodyText) {
        return lastPageNumber(Jsoup.parse(bodyText).body())
            .map(Mono::just)
            .orElse(Mono.empty());
    }

    Optional<Integer> lastPageNumber(@NotNull Element body) {
        Optional<String> optLinkToLastPage = body
            .getElementsByClass("pages")
            .stream()
            .flatMap(links ->
                links.getElementsByAttributeValue("title", "Last page")
                    .stream()
                    .map(el -> el.attr("href")))
            .findFirst();

        log.debug("Link to last page: " + optLinkToLastPage);

        return optLinkToLastPage.flatMap(link -> {
            Matcher matcher = Pattern.compile("(.*)pagenumber=(\\d+)").matcher(link);
            return matcher.find() ?
                Optional.of(Integer.valueOf(matcher.group(2))) :
                Optional.empty();
        });
    }

    private Flux<Post> parseToPostsFlux(String bodyText, Thread thread, int pageId) {
        return Flux.fromStream(postStreamFromPage(bodyText, thread, pageId));
    }

    private Flux<Thread> parseToThreadsFlux(String bodyText, Forum forum, int pageId) {
        return Flux.fromStream(threadStreamFromPage(bodyText, forum, pageId));
    }

    private Stream<Post> postStreamFromPage(String bodyText, Thread thread, int pageId) {
        return threadElementFromResponseBody(bodyText, thread)
            .map(threadElement -> postsFromThreadElement(threadElement, thread, pageId))
            .orElse(Stream.empty());
    }

    private Stream<Thread> threadStreamFromPage(String bodyText, Forum forum, int pageId) {
        return forumElementFromResponseBody(bodyText)
            .map(forumElement -> threadsFromForumElement(forumElement, forum, pageId))
            .orElse(Stream.empty());
    }


    private Optional<Element> threadElementFromResponseBody(String body, Thread thread) {
        if (null == body || body.isEmpty() || body.isBlank()) {
            return Optional.empty();
        }
        Element bodyElement = Jsoup.parse(body).body();
        Element threadElement = bodyElement.getElementById("thread");
        if (threadElement != null) {
            checkThreadId(threadElement, thread.getId());
            return Optional.of(threadElement);
        } else {
            return Optional.empty();
        }
    }

    private Optional<Element> forumElementFromResponseBody(String body) {
        if (null == body || body.isEmpty() || body.isBlank()) {
            return Optional.empty();
        }
        Element bodyElement = Jsoup.parse(body).body();
        Element threadElement = bodyElement.getElementById("forum");
        if (threadElement != null) {
            return Optional.of(threadElement);
        } else {
            return Optional.empty();
        }
    }

    private Stream<Post> postsFromThreadElement(Element threadElement, Thread thread, int pageId) {
        return threadElement.getElementsByClass("post").stream()
            .flatMap(postElement -> parsePost(postElement, thread, pageId));
    }

    private Stream<Thread> threadsFromForumElement(Element forumElement, Forum forum, int pageId) {
        return forumElement.getElementsByClass("thread").stream()
            .flatMap(threadElement -> parseThread(threadElement, forum, pageId));
    }

    private void checkThreadId(Element thread, long threadId) {
        String threadIdStr = thread.classNames().stream()
            .filter(s -> s.startsWith("thread:"))
            .map(s -> s.substring(7))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Can't get Thread Id"));

        if (!String.valueOf(threadId).equals(threadIdStr)) {
            throw new RuntimeException("Different thread id, expected " + threadId + " but got " + threadIdStr);
        }
    }

    private Optional<Integer> parsePostIdString(Optional<String> optPostIdStr) {
        try {
            return optPostIdStr.map(Integer::valueOf);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Stream<Thread> parseThread(Element threadElement, Forum forum, int pageId) {
        Optional<Integer> optThreadId = getThreadId(threadElement);
        if (optThreadId.isEmpty()) {
            return Stream.empty();
        }

        int threadId = optThreadId.get();

        Optional<String> optThreadTitle = threadElement
            .getElementsByClass("info").stream().flatMap(info -> info.getElementsByTag("a").stream()).findFirst().map(el -> el.text());
        if (optThreadTitle.isEmpty()) {
            return Stream.empty();
        }
        String threadTitle = optThreadTitle.get();

        // TODO
        // Thread page count
        // Thread author
        // Thread replies count
        // Thread views count
        // Thread Rating
        // Killed by time
        // Killed by name

        Thread thread = new Thread();
        thread.setId(threadId);
        thread.setName(threadTitle);
        thread.setForum(forum);

        return Stream.of(thread);

//        final Optional<String> authorName = postElement
//            .getElementsByClass("author")
//            .stream()
//            .map(Element::ownText)
//            .findFirst();
//
//        final Optional<String> rawPostBodyHtml = postElement
//            .getElementsByClass("postbody")
//            .stream()
//            .map(Element::html)
//            .findFirst();
//
//        final Optional<String> postBodyHtml = cleanBodyHtml(rawPostBodyHtml);
//
//        Optional<Integer> optPostId = getPostId(postElement);
//        if (optPostId.isEmpty()) {
//            return Stream.empty();
//        }
//        int postId = optPostId.get();
//
//        Optional<String> postDateString = postElement
//            .getElementsByClass("postdate")
//            .stream().map(Element::ownText)
//            .findFirst();
//
//        // eg Feb 1, 2020 05:24
//        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");
//        Optional<LocalDateTime> postDate = Optional.empty();
//        try {
//            postDate = postDateString.map(pds -> LocalDateTime.parse(pds, DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")));
//        } catch (DateTimeParseException ex) {
//            log.warn("Can't parse post date");
//        }
//
//        Optional<String> posterId = postElement
//            .getElementsByTag("td")
//            .stream()
//            .filter(el -> el.hasClass("userinfo"))
//            .flatMap(el -> el.classNames().stream())
//            .filter(s -> s.startsWith("userid-"))
//            .map(s -> s.substring(7))
//            .findFirst();
//
//        return (postDate.isPresent()
//            && authorName.isPresent()
//            && posterId.isPresent()
//            && postBodyHtml.isPresent()) ?
//            Stream.of(new Post(
//                postId,
//                pageId,
//                postDate.get(),
//                Instant.now(),
//                authorName.get(),
//                Integer.valueOf(posterId.get()),
//                postBodyHtml.get(),
//                thread))
//            : Stream.empty();
    }

    private Stream<Post> parsePost(Element postElement, Thread thread, int pageId) {
        final Optional<String> authorName = postElement
            .getElementsByClass("author")
            .stream()
            .map(Element::ownText)
            .findFirst();

        final Optional<String> rawPostBodyHtml = postElement
            .getElementsByClass("postbody")
            .stream()
            .map(Element::html)
            .findFirst();

        final Optional<String> postBodyHtml = cleanBodyHtml(rawPostBodyHtml);

        Optional<Integer> optPostId = getPostId(postElement);
        if (optPostId.isEmpty()) {
            return Stream.empty();
        }
        int postId = optPostId.get();

        Optional<String> postDateString = postElement
            .getElementsByClass("postdate")
            .stream().map(Element::ownText)
            .findFirst();

        // eg Feb 1, 2020 05:24
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");
        Optional<LocalDateTime> postDate = Optional.empty();
        try {
            postDate = postDateString.map(pds -> LocalDateTime.parse(pds, DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")));
        } catch (DateTimeParseException ex) {
            log.warn("Can't parse post date");
        }

        Optional<String> posterId = postElement
            .getElementsByTag("td")
            .stream()
            .filter(el -> el.hasClass("userinfo"))
            .flatMap(el -> el.classNames().stream())
            .filter(s -> s.startsWith("userid-"))
            .map(s -> s.substring(7))
            .findFirst();

        return (postDate.isPresent()
            && authorName.isPresent()
            && posterId.isPresent()
            && postBodyHtml.isPresent()) ?
            Stream.of(new Post(
                postId,
                pageId,
                postDate.get(),
                Instant.now(),
                authorName.get(),
                Integer.valueOf(posterId.get()),
                postBodyHtml.get(),
                thread))
            : Stream.empty();
    }

    private Optional<String> cleanBodyHtml(Optional<String> rawPostBodyHtml) {
        return rawPostBodyHtml.map(html ->
            html.replace("<!-- google_ad_section_start -->", "")
                .replace("<!-- google_ad_section_end -->", "")
                .replace("<p class=\"editedby\"> </p>", "")
                .trim());
    }

    private Optional<Integer> getPostId(Element postElement) {
        // Post IDs look like '#post508526226'
        final String postIDPrefix = "#post";
        final int postIDPrefixLength = postIDPrefix.length();

        final Optional<String> postIdString = postElement
            .getElementsByClass("postdate")
            .stream()
            .flatMap(el -> el.getElementsByTag("a").stream())
            .map(el -> el.attr("href"))
            .filter(s -> s.startsWith(postIDPrefix))
            .map(s -> s.substring(postIDPrefixLength))
            .findFirst();

        return parsePostIdString(postIdString);
    }

    private Optional<Integer> getThreadId(Element threadElement) {
        String idAttr = threadElement.attr("id");
        if (null == idAttr || idAttr.length() == 0) {
            return Optional.empty();
        } else if (idAttr.startsWith("thread")) {
            String idText = idAttr.substring(6);
            return Optional.of(Integer.parseInt(idText));
        }
        return Optional.empty();
    }
}
