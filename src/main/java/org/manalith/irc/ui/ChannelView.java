package org.manalith.irc.ui;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.manalith.irc.model.Connection;
import org.pircbotx.User;

import swing2swt.layout.BorderLayout;

public class ChannelView extends Composite implements IrcTab {
	public static final String EVENT_MESSAGE_SUBMITTED = "MessageSubmitted";

	private java.util.List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private StyledText messageOutput;
	private List list;
	private Text topic;
	private Text messageInput;
	private String channelName;
	private ChannelView instance;
	private Connection connection;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ChannelView(Composite parent, int style, String channelName,
			Connection connection) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		this.instance = this;
		this.connection = connection;

		list = new List(this, SWT.BORDER | SWT.MULTI);
		list.setLayoutData(BorderLayout.EAST);

		messageOutput = new StyledText(this, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		messageOutput.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				messageOutput.setTopIndex(messageOutput.getLineCount() - 1);
			}
		});

		messageOutput.setLayoutData(BorderLayout.CENTER);

		topic = new Text(this, SWT.BORDER);
		topic.setLayoutData(BorderLayout.NORTH);

		messageInput = new Text(this, SWT.BORDER);
		messageInput.setLayoutData(BorderLayout.SOUTH);
		messageInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					onAction(new Action(EVENT_MESSAGE_SUBMITTED, messageInput,
							instance));
				}
			}

			public void keyReleased(KeyEvent e) {
				// ignore
			}
		});

		this.channelName = channelName;
	}

	public Text getMessageInput() {
		return messageInput;
	}

	public StyledText getMessageOutput() {
		return messageOutput;
	}

	public void printMessage(String message) {
		messageOutput.append(message + "\n");
	}

	public void printAsyncMessage(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				messageOutput.append(message + "\n");
			}
		});
	}

	public List getUserList() {
		return list;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	private void onAction(Action action) {
		for (ActionListener listener : actionListeners) {
			listener.onAction(action);
		}
	}

	public String getChannelName() {
		return channelName;
	}

	public void setTopic(String topic) {
		this.topic.setText(topic);
	}

	public void updateUserList(final Set<User> users) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				for (User u : users) {
					getUserList().add(u.getNick());
				}
				getUserList().redraw();
			}
		});
	}

	public Connection getConnection() {
		return connection;
	}
}
