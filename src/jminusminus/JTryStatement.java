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
    }
}
