package pl.edu.pw.appt.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;
import net.sourceforge.plantuml.StringUtils;
import pl.edu.pw.appt.GUI;

/**
 * @author Kacper
 */
public class Messages {

    private final Map<String, SortedSet<Message>> systems = new HashMap<>();
    private final Map<String, Integer> stopCounter = new HashMap<>();
    private final GUI gui;

    public Messages(GUI gui_) {
        this.gui = gui_;
    }

    public void add(String systemName, Message message) {
        if (!systems.containsKey(systemName)) {
            SortedSet<Message> messages = new TreeSet<>();
            systems.put(systemName, messages);
        }
        systems.get(systemName).add(message);
    }

    public void clear(String systemName) {
        if (systems.containsKey(systemName)) {
            systems.get(systemName).clear();
        }
    }

    public boolean isDependent1(int i, int i2, String systemName) {
        SortedSet<Message> messages = systems.get(systemName);
        Message mStart = null;
        int iStart = 0;
        int iZ = 0;
        for (Message m : messages) {
            iZ++;
            if (iZ == i) {
                System.out.println("Rec: " + m.messageType.toString());
                mStart = m;
                iStart = i;
            }

            if (iZ == i2 && mStart != null) {
                System.out.println("Rec: " + m.messageType.toString());
                System.out.println("Deps: " + isDependent(mStart, iStart, m, i, messages));
                return isDependent(mStart, iStart, m, i, messages);
            }
        }

        return false;
    }

    public boolean isDependent(Message m1, int i, Message m2, int i2, SortedSet<Message> messages) {
        boolean send = m1.messageType == Message.Type.Send;
        boolean found = false;
        Message curr = m1;
        int zIndex = 0;
        for (Message z : messages) {
            if (i > zIndex++) {
                continue;
            }

            if (!send) {
                if (m1.id != z.id && (m1.from.equals(z.from))) {
                    break;
                }

                if (m1.id != z.id && m1.from.equals(z.to) && !z.from.equals(curr.to)) {
                    break;
                }

                if (!curr.to.equals(z.from) && !curr.to.equals(z.to)) {
                    continue;
                }

                if (!curr.to.equals(z.from)) {
                    break;
                }

                if (z.to.equals(m2.from) && z.id != m2.id) {
                    found = true;
                    break;
                }

                if (z.messageType != Message.Type.Send) {
                    break;
                }

                curr = z;
                send = true;
            } else {
                if (!curr.equals(z)) {
                    continue;
                }

                if (z.to.equals(m2.from) && z.id != m2.id) {
                    found = true;
                    break;
                }

                if (z.messageType != Message.Type.Receive) {
                    break;
                }

                curr = z;
                send = false;

            }
        }
        return found;
    }

    Message mStart = null;
    int iStart = 0;

    public String toUml(String systemName, String start, String stop) {
        SortedSet<Message> messages = systems.get(systemName);
        TreeMap<Message, Message> paired = new TreeMap<>();
        List<Integer> endings = new ArrayList();
        assert messages != null;
        StringJoiner ret = new StringJoiner(System.getProperty("line.separator"));
        int i = 0;
        boolean bRun = StringUtils.isEmpty(start);
        boolean bStop = false;

        int zIndex = -1;
        for (Message m : messages) {
            i++;
            if (!StringUtils.isEmpty(stop) && stopCounter.containsKey(systemName)) {
                if (i < stopCounter.get(systemName)) {
                    continue;
                }
            }
            if (!StringUtils.isEmpty(start) && start.equals(m.signalName)) {
                bRun = true;
            }
            if (!bRun) {
                continue;
            } else if (!StringUtils.isEmpty(stop) && stop.equals(m.signalName)) {
                bStop = true;
            }

            if (m.messageType == Message.Type.Send && (endings.size() < 1 || i >= endings.get(endings.size() - 1))) {
                boolean send = true;
                boolean found = false;
                Message curr = m;
                zIndex = 0;
                for (Message z : messages) {
                    if (i > zIndex++) {
                        continue;
                    }

                    if (!send) {

                        if (m.id != z.id && (m.from.equals(z.from))) {
                            break;
                        }

                        if (m.id != z.id && m.from.equals(z.to) && !z.from.equals(curr.to)) {
                            break;
                        }

                        if (!curr.to.equals(z.from) && !curr.to.equals(z.to)) {
                            continue;
                        }

                        if (!curr.to.equals(z.from)) {
                            break;
                        }

                        if (z.messageType != Message.Type.Send || !z.signalName.equals(m.signalName)) {
                            break;
                        }

                        curr = z;
                        send = true;
                    } else {
                        if (!curr.equals(z)) {
                            continue;
                        }

                        if (z.messageType != Message.Type.Receive) {
                            break;
                        }

                        curr = z;
                        send = false;

                        if (z.to.equals(m.from) && z.id != m.id) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    zIndex = -1;
                }

                if (found) {
                    for (int end : endings) {
                        if (zIndex == end) {
                            zIndex = -1;
                        }
                    }
                }

//                if (zIndex != -1) {
//                    endings.add(zIndex);
//                    ret.add("group zal" + zIndex);
//                }
            }

            Message.Type t = m.messageType;
            Boolean bFoundPair = false;
            int pairIndex = 0;
            for (Message pair : messages) {
                if (t == Message.Type.Send) {
                    if (i > pairIndex++) {
                        continue;
                    }
                    // Poprawne parowanie na podstawie unikalnych ID dla par wiadomości.
                    if (pair.id == m.id && pair.messageType == Message.Type.Receive && pair.from.equals(m.from) && pair.to.equals(m.to) && pair.signalName.equals(m.signalName)) {
                        ret.add(m.from + " -> " + m.to + " : " + m.signalName + " : " + i);
                        if (m.globalId + 1 != pair.globalId) {
                            ret.add(m.from + " ->o " + m.to + " : " + m.signalName + " : " + i);
                            ret.add("activate " + m.from);
                        } else {
                            ret.add(m.from + " -> " + m.to + " : " + m.signalName + " : " + i);
                        }
                        paired.put(pair, m);
                        bFoundPair = true;
                        break;
                    }
                } else if (t == Message.Type.Receive) {
                    if (i < pairIndex++) {
                        break;
                    }

                    if (paired.containsKey(m)) {
                        if (m.globalId - 1 != paired.get(m).globalId) {
                            ret.add(m.from + " o-> " + m.to + " : " + m.signalName + " : " + i);
                            ret.add("deactivate " + m.from);
                        }
                        bFoundPair = true;
                        break;
                    }
                }
            }
            if (!bFoundPair) {
                if (t == Message.Type.Receive) {
                    ret.add(m.from + " o-> " + m.to + " : " + m.signalName + " : " + i);
                } else if (t == Message.Type.Send) {
                    ret.add(m.from + " ->o " + m.to + " : " + m.signalName + " : " + i);
                }
            }

            for (int end : endings) {
                if (end == i) {
                    ret.add("end zal" + end);
                    break;
                }
            }

            if (bStop) {
                if (!gui.pauseOn) {
                    stopCounter.put(systemName, i);
                }
                gui.pauseOn = true;
                break;
            }
        }

        return ret.toString();
    }

    public String toString(String systemName) {
        StringJoiner result = new StringJoiner(System.getProperty("line.separator"));
        if (systems.containsKey(systemName)) {
            systems.get(systemName).stream().forEach((e) -> {
                result.add(e.toString());
            });
        }
        return result.toString();
    }
}
