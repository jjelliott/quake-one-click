package q1.installer.unpack;

public interface Extractor {

  boolean handles(String extension);

  void extract(String path);
}
