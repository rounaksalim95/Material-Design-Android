package livelessons.imagestreamgang.streams;

import android.util.Log;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import livelessons.imagestreamgang.filters.Filter;
import livelessons.imagestreamgang.filters.FilterDecoratorWithImage;
import livelessons.imagestreamgang.utils.Image;

import static java.util.stream.Collectors.toList;

/**
 * Customizes ImageStream to use a Java 8 stream to download, process,
 * and store images sequentially.
 */
public class ImageStreamSequential 
       extends ImageStream {
    /**
     * Constructor initializes the superclass and data members.
     */
    public ImageStreamSequential(Filter[] filters,
                                 Iterator<List<URL>> urlListIterator,
                                 Runnable completionHook) {
        super(filters, urlListIterator, completionHook);
    }

    /**
     * Perform the ImageStream processing, which uses a Java 8 stream
     * to download, process, and store images concurrently.
     */
    @Override
    protected void processStream() {
        List<Image> collect = getInput()
            // Sequentially process each URL in the input List.
            .stream()

            // Only include URLs that have not been already cached.
            .filter(not(this::urlCached))

            // Transform URL -> Image (download each image via
            // its URL).
            .map(this::makeImage)

            // Map each image to a stream containing the filtered
            // versions of the image.
            .flatMap(this::applyFilters)

            // Terminate the stream.
            .collect(toList());

        Log.d(TAG, "processing of "
                + (collect != null ? collect.size() : "0")
                + " image(s) is complete");
    }

    /**
     * @return true if the @a url is already in the cache, else false.
     */
    @Override
    protected boolean urlCached(URL url) {
        // Iterate through the list of filters and sequentially check
        // to see which ones are already cached.
        long count = mFilters
            .stream()
            .filter(filter ->
                    urlCached(url, filter.getName()))
            .count();

        // A count > 0 means the url has already been cached.
        return count > 0;
    }

    /**
     * Apply the filters to each @a image sequentially.
     */
    private Stream<Image> applyFilters(Image image) {
        return mFilters
            // Iterate through the list of filters and apply each
            // filter sequentially.
            .stream()

            // Create an OutputDecoratedFilter for each image.
            .map(filter -> makeFilterDecoratorWithImage(filter, image))

            // Filter the image and store it in an output file.
            .map(FilterDecoratorWithImage::run);
    }
}
