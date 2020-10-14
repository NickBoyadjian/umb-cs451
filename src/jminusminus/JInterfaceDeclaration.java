package jminusminus;

import java.util.ArrayList;


public class JInterfaceDeclaration extends JAST implements JTypeDecl {

    // Interface modifiers.
    private ArrayList<String> mods;

    // Interface name.
    private String name;

    // Super classes
    private ArrayList<TypeName> superClasses;

    // Interface block.
    private ArrayList<JMember> interfaceBlock;


    /**
     * Constructs an AST node for a class declaration.
     *
     * @param line       line in which the class declaration occurs in the source file.
     * @param mods       class modifiers.
     * @param name       class name.
     * @param superClasses  super class type.
     * @param interfaceBlock class block.
     */
    public JInterfaceDeclaration(int line, ArrayList<String> mods, String name, ArrayList<TypeName> superClasses, ArrayList<JMember> interfaceBlock) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.superClasses = superClasses;
        this.interfaceBlock = interfaceBlock;
    }


    /**
     * {@inheritDoc}
     */
    public void declareThisType(Context context) {
    }

    /**
     * {@inheritDoc}
     */
    public void preAnalyze(Context context) {
    }

    /**
     * {@inheritDoc}
     */
    public String name() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public Type thisType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Type superType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public JAST analyze(Context context) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {

    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JInterfaceDeclaration:" + line, e);

        // superclasses
        if (this.superClasses != null && this.superClasses.size() > 0) {
            StringBuilder superClassesString = new StringBuilder();
            for (int i = 0; i < this.superClasses.size(); i++) {
                superClassesString.append(superClasses.get(i).toString());
            }
        }

        // modifiers
            ArrayList<String> modString = new ArrayList<String>();
            for (int i = 0; i < mods.size(); i++)
                modString.add("\"" + mods.get(i) + "\"");
            e.addAttribute("modifiers", modString);

        // name
        e.addAttribute("name", this.name);

        // interface blocks
        if (interfaceBlock != null && interfaceBlock.size() > 0) {
            for (JMember member : interfaceBlock) {
                ((JAST) member).toJSON(e);
            }
        }
    }

}
