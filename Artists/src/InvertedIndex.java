import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvertedIndex {
	HashMap<String, List<Integer>> invertedIndex; // Stores an inverted list of
													// artists and the line
													// number where they appear
	ArrayList<String> indexLong; // Stores all artists who have appeared more
									// than 50 times
	String filePath; // File location of input file

	// This function Sets the inverted index of all the artists and also
	// qualified artists
	void setInvertedIndex() {
		indexLong = new ArrayList<String>();
		invertedIndex = new HashMap<String, List<Integer>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					this.filePath));
			String line;
			int lineNumer = 0;
			while ((line = reader.readLine()) != null) {
				lineNumer++;
				String lineUTF8 = new String(line.getBytes(), "UTF-8");
				String[] artists = lineUTF8.split(",");
				for (String artist : artists) {
					if (invertedIndex.containsKey(artist)) {
						invertedIndex.get(artist).add(lineNumer);
						// Whenever the List in inverted index reaches size of
						// 50 it is added to indexLong
						if (invertedIndex.get(artist).size() == 50) {
							this.indexLong.add(artist);
						}
					} else {
						List<Integer> l = new ArrayList<Integer>();
						l.add(lineNumer);
						invertedIndex.put(artist, l);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Constructor sets filePath, invertedIndex and indexLong
	InvertedIndex(String fileName) {
		this.filePath = fileName;
		this.setInvertedIndex();
	}

	// This functions compares to sorted Lists and returns true if the size of
	// intersection is greater than 50
	// We know that list are sorted as we parse through the file sequentially
	// and add them to inverted list
	static boolean returnIfMorethan50(List<Integer> l1, List<Integer> l2) {
		int i = 0, j = 0, count = 0;
		int l1Size = l1.size();
		int l2Size = l2.size();
		while (i < l1Size && j < l2Size) {
			// If the remaining number of elements + count is less than 50
			// This reduces unnecessary comparisons
			if (l1Size - i + count < 50 || l2Size - j + count < 50) {
				return false;
			} else if (l1.get(i) < l2.get(j))
				i++;
			else if (l2.get(j) < l1.get(i))
				j++;
			else {
				count++;
				i++;
				j++;
				if (count == 50) {
					return true;
				}
			}

		}

		return false;
	}

	public static void main(String[] args) {
		try {
			InvertedIndex id = new InvertedIndex("src/Artist_lists_small.txt");

			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("src/output.txt"), "UTF-8"));
			// For loops through each distinct pair and check if the size of
			// intersection is greater than 50
			for (int i = 0; i < id.indexLong.size() - 1; i++) {
				for (int j = i + 1; j <= id.indexLong.size() - 1; j++) {
					List<Integer> l1 = id.invertedIndex
							.get(id.indexLong.get(i));
					List<Integer> l2 = id.invertedIndex
							.get(id.indexLong.get(j));
					if (InvertedIndex.returnIfMorethan50(l1, l2)) {
						out.write(id.indexLong.get(i) + "------"+id.indexLong.get(j)+"\n");
						System.out.println(id.indexLong.get(i) + "------"+ id.indexLong.get(j));
					}
				}
			}
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
