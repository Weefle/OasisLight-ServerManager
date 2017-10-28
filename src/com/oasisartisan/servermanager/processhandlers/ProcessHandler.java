/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oasisartisan.servermanager.processhandlers;

import com.oasisartisan.servermanager.consolecommunication.Printer;
import com.oasisartisan.servermanager.Utilities;
import com.oasisartisan.servermanager.objects.BackupProfile;
import com.oasisartisan.servermanager.objects.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author OmarAlama
 */
public interface ProcessHandler {

    public Process sendCommandToServer(Server server, String cmd) throws IOException, InterruptedException;

    public Process startServerProcess(Server server) throws IOException;

    public Process killServerProcess(Server server, boolean gracefull) throws IOException, InterruptedException;

    public ArrayList<String> listProcesses() throws IOException, InterruptedException;

    public boolean hasActiveProcess(Server server) throws IOException, InterruptedException;

    public boolean hasActiveProcess(Server server, String processListElement);

    public boolean clearConsole();

    public Process executeBackup(Server s, BackupProfile bp) throws IOException;

    public String translateBackupOutputToProgress(String[] output);
    
    public boolean checkPrerequisites() throws InterruptedException;

    public void waitForKeyPress();

    public static boolean finishProcess(Process p, String pname) throws InterruptedException{
        boolean interrupted = false;
        try {
            p.waitFor(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            interrupted = true;
            p.destroy();
            Printer.printBackgroundInfo(pname, "The process has been interrupted.");
            p.waitFor(2, TimeUnit.SECONDS);
        }
        if (!p.isAlive()) {
            try {
                Utilities.printStream(p.getErrorStream());
            } catch (IOException e) {
                Printer.printError("[" + pname + "] Failed to read error stream for process.", e);
            }
            if(interrupted)
            {
                throw new InterruptedException();
            }
            return true;
        } else {
            p.destroyForcibly();
            Printer.printError("[" + pname + "] The process is taking too long to finish. destroying it.", null);
            if(interrupted)
            {
                throw new InterruptedException();
            }
            return false;
        }
    }
}
