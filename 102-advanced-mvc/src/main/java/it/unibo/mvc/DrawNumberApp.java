package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import it.unibo.mvc.Configuration.Builder;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *              the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view : views) {
            view.setObserver(this);
            view.start();
        }
        this.model = createModelFromConfig("config.yml");
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view : views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view : views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        // System.exit(0);
        for (final var view : this.views) {
            view.stop();
        }
    }

    private DrawNumber createModelFromConfig(final String fileName) {
        Objects.requireNonNull(fileName);
        // Default values if value is not set in config file
        final Builder configBuilder = new Builder();
        if (ClassLoader.getSystemResource(fileName) != null) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName), "UTF-8"))) {
                String line = reader.readLine();
                while (line != null) {
                    final StringTokenizer tokenizer = new StringTokenizer(line, ": ");
                    final String configName = tokenizer.nextToken();
                    final Integer configValue = Integer.parseInt(tokenizer.nextToken());
                    if ("minimum".equalsIgnoreCase(configName)) {
                        configBuilder.setMin(configValue);
                    } else if ("maximum".equalsIgnoreCase(configName)) {
                        configBuilder.setMax(configValue);
                    } else if ("attemps".equalsIgnoreCase(configName)) {
                        configBuilder.setAttempts(configValue);
                    }
                    line = reader.readLine();
                }
            } catch (IOException e1) {
                for (final var view : views) {
                    view.displayError("An I/O error occurred");
                }
            }
        }
        // Configuration correctness check missing
        return new DrawNumberImpl(configBuilder.build());
    }

    /**
     * @param args
     *             ignored
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl(), new PrintStreamView(System.out));
    }

}
