import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;



public class Parser {
	private Map<Character, Map<Character, String>> table;
	
	private Set<Character> terminals;
	
	private Set<Character> variables;
	
	private char startVariable;
	
	private  List<String> productions;
	public String fileName;
	
	public Parser(String FileName){
		this.table = new TreeMap<Character, Map<Character, String>>();
		this.startVariable = 'P';
		this.fileName = FileName;
		this.productions = new ArrayList<String>();
		this.terminals = new TreeSet<Character>(Arrays.asList('(', '0', '1', '2', '3', '"', 'a', 'b', 'c', 'd', '{', '}', ')', 'e', 'l', 's', 'i', 'f', ';', 'p', 'r', 't', 'n', '+', '-', '*', '$'));
		this.variables = new TreeSet<Character>(Arrays.asList('P', 'L', 'R', 'B', 'W', 'C', 'D', 'E', 'O', 'V', 'T'));
	}
	public void createTable(){
		//table = new TreeMap<Character, Map<Character, String>>();
		for(char variable : variables)
			this.table.put(variable, new TreeMap<Character, String>());

		this.table.get('T').put('a', "a");
		this.table.get('T').put('b', "b");
		this.table.get('T').put('c', "c");
		this.table.get('T').put('d', "d");
		
		this.table.get('V').put('0', "0");
		this.table.get('V').put('1', "1");
		this.table.get('V').put('2', "2");
		this.table.get('V').put('3', "3");
		
		this.table.get('O').put('+', "+");
		this.table.get('O').put('-', "-");
		this.table.get('O').put('*', "*");
		
		this.table.get('E').put('0', "V");
		this.table.get('E').put('1', "V");
		this.table.get('E').put('2', "V");
		this.table.get('E').put('3', "V");
		this.table.get('E').put('(', "(EOE)");
		
		this.table.get('C').put('i', "ifE{P}D");

		this.table.get('P').put('p', "LR");
		this.table.get('P').put('i', "LR");
		
		this.table.get('L').put('p', "printB");
		this.table.get('L').put('i', "C");
		
		this.table.get('B').put('0', "E;");
		this.table.get('B').put('1', "E;");
		this.table.get('B').put('2', "E;");
		this.table.get('B').put('3', "E;");
		this.table.get('B').put('(', "E;");
		this.table.get('B').put('"', "\"W;");
		
		this.table.get('W').put('a', "TW");
		this.table.get('W').put('b', "TW");
		this.table.get('W').put('c', "TW");
		this.table.get('W').put('d', "TW");
		this.table.get('W').put('"', "#");
		
		this.table.get('R').put('$', "#");
		this.table.get('R').put('i', "LR");
		this.table.get('R').put('p', "LR");
		this.table.get('R').put('}', "#");
		
		this.table.get('D').put('e', "else{P}");
		this.table.get('D').put('$', "#");
		this.table.get('D').put('i', "#");
		this.table.get('D').put('p', "#");
		this.table.get('D').put('}', "#");
		
	}
	
	public void parse(String str){
		str += "$";
		System.out.println(str);
		Stack<Character> stack = new Stack<Character>();
		int i = 0;
		stack.push(this.startVariable);
		while(!stack.empty()){
			char variable = stack.peek();
			char terminal = str.charAt(i);
			//System.out.println("variable = " + variable + " terminal = " + terminal);
			if(variable == terminal){
				i++;
				stack.pop();
			}else if(!terminals.contains(terminal)){
				System.out.println("ERROR_INVALID_SYMBOL");
				return;
			}else if(this.variables.contains(variable)){
				stack.pop();
				String production = this.table.get(variable).get(terminal);
				if(production == null){
					System.out.println("REJECTED");
					return;
				}
				this.productions.add(variable + " -> " + production);
				String b = getStackString(stack);
				//System.out.println(str.substring(i) + " ----- " + b);
				if(production == "#")
					continue;
				//System.out.println("Production = " + production);
				for(int p = production.length() - 1; p >= 0; --p)
					stack.push(production.charAt(p));
			}else if(terminals.contains(terminal)){
				System.out.println("REJECTED");
				return;
			}
		}
		if(i != str.length() - 1){
			System.out.println("REJECTED");
			return;			
		}
		System.out.println("ACCEPTED");
		//for(String production : productions)
			//System.out.println(production);		//prints stack trace of productions used
	}
	@SuppressWarnings ("unchecked")
	public String getStackString(Stack<Character> stack){
		Stack<Character> clone = (Stack<Character>) stack.clone();
		int length = clone.size();
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < length; i++){
			s.append(clone.pop());
		}
		System.out.println("string is " + s.toString());
		return s.toString();
	}
	public String readInput(){

		BufferedReader br = null;
		FileReader fr = null;
        String FILENAME = this.fileName;
        try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));
			StringBuilder input = new StringBuilder();
			while ((sCurrentLine = br.readLine()) != null) {
                //Current line, Strip out whitespace and push characters to stack
                String s = sCurrentLine.replaceAll("\\s+","");
                input.append(s);
			}
			return input.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
        }
		return null;
    }

	public static void main(String[] args) {
		if(args.length < 1){
            System.exit(0);
        }
		
        Parser parser = new Parser(args[0]);
		String input = parser.readInput();
		parser.createTable();
		parser.parse(input);
		
		//parse("if/");
		
		//parse("1");
        //parse("(1 + 0)");
	}

}
