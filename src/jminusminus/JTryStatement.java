package jminusminus;

import java.util.ArrayList;

public class JTryStatement extends JStatement {
    private JBlock tryBlock;
    private ArrayList<JFormalParameter> parameters;
    private ArrayList<JBlock> catchBlocks;
    private JBlock finallyBlock;

    JTryStatement (int line, JBlock tryBlock, ArrayList<JFormalParameter> parameters,
                   ArrayList<JBlock> catchBlocks, JBlock finallyBlock) {
        super(line);
        this.tryBlock = tryBlock;
        this.parameters = parameters;
        this.catchBlocks = catchBlocks;
        this.finallyBlock = finallyBlock;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
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
        json.addChild("JTryStatement:" + line, e);

        JSONElement tryBlock = new JSONElement();
        e.addChild("TryBlock", tryBlock);
        this.tryBlock.toJSON(tryBlock);

        for (int i = 0; i < parameters.size(); i++) {
            // catch block parent
            JSONElement catchBlock = new JSONElement();
            e.addChild("CatchBlock", catchBlock);

            // parameters
            ArrayList<String> value = new ArrayList<String>();
            value.add("\"" + parameters.get(i).name().toString() + "\", \"" + parameters.get(i).type().toString() + "\"");
            catchBlock.addAttribute("parameter", value);

            catchBlocks.get(i).toJSON(catchBlock);
        }

//        JSONElement parameters = new JSONElement();
//        e.addChild("Parameters", parameters);
//        this.parameters.forEach(cb -> cb.toJSON(parameters));
//
//        JSONElement catchBlock = new JSONElement();
//        e.addChild("CatchBlock", catchBlock);
//        this.catchBlocks.forEach(cb -> cb.toJSON(catchBlock));

        JSONElement finallyBlock = new JSONElement();
        e.addChild("FinallyBlock", finallyBlock);
        this.finallyBlock.toJSON(finallyBlock);
    }
}
