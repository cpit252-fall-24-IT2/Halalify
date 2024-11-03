import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class VideoProcessor {

    public File uploadVideo() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Video File");

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Video Files", "mp4", "avi", "mov"));

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            final long MAX_SIZE = 1073741824L;
            if (selectedFile.length() > MAX_SIZE) {
                System.err.println("File size is above the 1GB limit. Please choose a smaller file.");
                return null;
            }

            System.out.println("Video selected: " + selectedFile.getAbsolutePath());

            return selectedFile;
        } else {
            System.out.println("No video selected.");
            return null;
        }
    }
}
