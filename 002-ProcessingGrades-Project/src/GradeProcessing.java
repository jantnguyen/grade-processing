import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class GradeProcessing {
	private static String path = "/Users/giangnguyen/Downloads/students.txt";
	private static String outputPath = "/Users/giangnguyen/Downloads/students-output.txt";
	
	public static void main(String[] args) throws IOException {
		ArrayList<String[]> inputFile = importFile(path);
		HashMap<String, double[]> gradesByStudent = parseInputData(inputFile);
		HashMap<String, Double> averagesByStudent = calculateAverageGrade(gradesByStudent);		
		HashMap<String, String> letterGradeByStudent = calculateLetterGrade(averagesByStudent);
		double totalAverage = calculateAverage(averagesByStudent.values());
		ArrayList<String[]> outputFile = formatDataForOutput(
			inputFile,
			averagesByStudent,
			letterGradeByStudent,
			totalAverage
		);
		
		exportResults(outputFile);
	}

	private static ArrayList<String[]> importFile(String path) {
		ArrayList<String[]> inputFile = new ArrayList<String[]>();
		try {
			String row = "";
			
			BufferedReader br = new BufferedReader(new FileReader(path));			
			while((row = br.readLine()) != null) {
				String[] values = row.split("    ");
				ArrayList<String> listValues = new ArrayList<String>(Arrays.asList(values));
				listValues.replaceAll(value -> value.trim());
				String[] convertedArray = new String[ listValues.size()];
				listValues.toArray(convertedArray);
				inputFile.add(convertedArray);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return inputFile;
	}
	
	private static HashMap<String, double[]> parseInputData(ArrayList<String[]> inputFile) {
		HashMap<String, double[]> data = new HashMap<String, double[]>();
		
		for (int i=0; i<inputFile.size(); i++) {
			String[] values = inputFile.get(i);
			if (i==0) {
				continue;
			}
			
			double[] doubleValues = Arrays.stream(Arrays.copyOfRange(values, 2, values.length))
	                .mapToDouble(Double::parseDouble)
	                .toArray();
			data.put(values[0], doubleValues);
		}	
		
		return data;
	}
	
	private static HashMap<String, Double> calculateAverageGrade(HashMap<String, double[]> gradesByStudent) {
		HashMap<String, Double> averages = new HashMap<String, Double>();
		gradesByStudent.forEach((student, grades) -> averages.put(student, calculateAverage(grades)));
		return averages;
	}
	
	private static double calculateAverage(double[] grades) {
		DecimalFormat format = new DecimalFormat("##.#");
		double sum = 0.0;
		for (double grade:grades)
			sum += grade;
		return Double.parseDouble(format.format(sum / grades.length));
	}
	
	private static double calculateAverage(Collection<Double> grades) {
		DecimalFormat format = new DecimalFormat("##.#");
		double sum = 0.0;
		for (double grade:grades)
			sum += grade;
		return Double.parseDouble(format.format(sum / grades.size()));
	}

	private static HashMap<String, String> calculateLetterGrade(HashMap<String, Double> averagesByStudent) {
		HashMap<String, String> letterGrades = new HashMap<String, String>();
		averagesByStudent.forEach((student, average) -> letterGrades.put(student, calculateLetter(average)));
		return letterGrades;
	}
	
	private static String calculateLetter(double average) {
		String letterGrade;
		if (average >= 90) {
			letterGrade = "A";
		} else if (average >= 80 && average < 90) {
			letterGrade = "B";
		} else if (average >= 70 && average < 80) {
			letterGrade = "C";
		} else if (average >= 60 && average < 70) {
			letterGrade = "D";
		} else if (average < 59) {
			letterGrade = "F";
		} else {
			letterGrade = "Error";
		}
		return letterGrade;
	}

	private static ArrayList<String[]> formatDataForOutput(ArrayList<String[]> inputData, HashMap<String, Double> averages, HashMap<String, String> letterGrades, double classAverage) {
		ArrayList<String[]> outputData = new ArrayList<String[]>();
		
		for (int i=0; i<inputData.size(); i++) {
			String[] values = inputData.get(i);
			ArrayList<String> listValues = new ArrayList<String>(Arrays.asList(values));

			if (i==0) {
				String[] newColumns = {"Avg.", "Letter", "Class Avg."};
				listValues.addAll(Arrays.asList(newColumns));
			} else {
				String studentName = values[0];
				String[] newColumns = {
						averages.get(studentName).toString(),
						letterGrades.get(studentName)
				};
				listValues.addAll(Arrays.asList(newColumns));
			}
			
			String[] convertedArray = new String[ listValues.size()];
			listValues.toArray(convertedArray);
			
			outputData.add(convertedArray);
		}	

		String[] classAverageRow = new String[ outputData.get(0).length];
		Arrays.fill(classAverageRow, "");
		classAverageRow[outputData.get(0).length - 1] = String.valueOf(classAverage);
		outputData.add(classAverageRow);
		
		return outputData;
	}

    private static void exportResults(ArrayList<String[]> outputFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
		outputFile.forEach(row -> {
    			try {   				
					bw.newLine();
					System.out.println(Arrays.deepToString(row)
							.replace(",", "\t")
							.replace("[", "")
							.replace("]", ""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			for (int i=0; i< row.length; i++) {
    				try {
						bw.write(row[i]);
						bw.write("\t");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
		});
		bw.flush();
		bw.close();
    }
}
